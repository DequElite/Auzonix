package dequelite.auzonix.zoosystem.au_manadger.monkey_test.run;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import dequelite.auzonix.zoosystem.ai.GenerateScenarioIntermediateStepPrompt;
import dequelite.auzonix.zoosystem.app_analyzer.browser_analyzers.AnalyzeBrowserDiscoveryService;
import dequelite.auzonix.zoosystem.app_analyzer.browser_analyzers.CompareAnalyzedPagesService;
import dequelite.auzonix.zoosystem.app_analyzer.browser_analyzers.dto.ComparedPagesDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.*;
import dequelite.auzonix.zoosystem.au_manadger.au_types.dto.AuTypeDto;
import dequelite.auzonix.zoosystem.au_manadger.monkey_test.run.dto.ConsoleErrorDto;
import dequelite.auzonix.zoosystem.au_manadger.monkey_test.run.dto.DecidedScenarioDto;
import dequelite.auzonix.zoosystem.browser_manadger.dto.BrowserSession;
import dequelite.auzonix.zoosystem.browser_manadger.dto.BrowserSettings;
import dequelite.auzonix.zoosystem.browser_manadger.factory.BrowserFactory;
import dequelite.auzonix.zoosystem.error_collector.ErrorCollectorService;
import dequelite.auzonix.zoosystem.error_collector.dto.ErrorType;
import dequelite.auzonix.zoosystem.error_collector.dto.MonkeyTestErrorDto;
import dequelite.auzonix.zoosystem.error_collector.dto.SystemErrorDto;
import dequelite.auzonix.zoosystem.error_collector.dto.TestErrorDto;
import dequelite.auzonix.zoosystem.event.disparcher.EventDispatcher;
import dequelite.auzonix.zoosystem.event.dto.SystemEventDto;
import dequelite.auzonix.zoosystem.scenario_manadger.dto.ChSIntermediateStepDto;
import dequelite.auzonix.zoosystem.scenario_manadger.dto.ChSStepDto;
import dequelite.auzonix.zoosystem.scenario_manadger.dto.ChaosScenarioDto;
import dequelite.auzonix.zoosystem.scenario_manadger.generating.GenerateScenarioIntermediateStepService;
import dequelite.auzonix.zoosystem.test_action_collector.dto.MonkeyTestActionDto;
import dequelite.auzonix.zoosystem.test_action_collector.dto.TestActionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class RunMonkeyTestService {

    private final EventDispatcher eventDispatcher;

    private final LocatorService locatorService;
    private final ActService actService;
    private final BrowserEventListenerService browserEventListenerService;

    private final ErrorCollectorService errorCollectorService;

    private final BrowserFactory browserFactory;

    private final AnalyzeBrowserDiscoveryService analyzeBrowserDiscoveryService;
    private final CompareAnalyzedPagesService compareAnalyzedPagesService;
    private final GenerateScenarioIntermediateStepService generateScenarioIntermediateStepService;

    public void run(
            ChaosScenarioDto chaosScenario,
            BrowserSettings browserSettings,
            PageDto pageDto,
            AnalyzedAppPageDto analyzedAppPage,
            AnalyzedAppIdeaDto appIdeaAnalysis,
            AuTypeDto auType
            ) {
        List<ChSStepDto> stepsToExecute = new ArrayList<>(chaosScenario.steps());
        AnalyzedAppPageDto currentPageAnalyze = new AnalyzedAppPageDto(
                analyzedAppPage.metadata(),
                analyzedAppPage.elementsCount(),
                analyzedAppPage.elementsCountByRoles(),
                analyzedAppPage.elements()
        );

        List<ChSStepDto> executedSteps = new ArrayList<>();

        List<TestErrorDto> errors = new ArrayList<>();
        UUID testId = UUID.randomUUID();

        AtomicInteger currentStepNumber = new AtomicInteger(0);
        int totalSteps = stepsToExecute.size();
        long scenarioStart = System.currentTimeMillis();

        publishEvent(new MonkeyTestActionDto(
                "INFO [SCENARIO] START id=%s name='%s' auType=%s steps=%d"
                        .formatted(
                                chaosScenario.id(),
                                chaosScenario.name(),
                                chaosScenario.auType(),
                                totalSteps
                        ),
                testId,
                TestActionType.STARTED,
                chaosScenario.id(),
                currentStepNumber.get()
        ));

        try (BrowserSession browserSession = browserFactory.createSession(browserSettings)) {
            BrowserContext browserContext = browserSession.context();
            try (Page page = browserContext.newPage()) {
                this.browserEventListenerService.register(
                        errors,
                        page,
                        currentStepNumber
                );

                page.navigate(chaosScenario.targetUrl());

                publishEvent(new MonkeyTestActionDto(
                        "INFO [NAV] opened %s (title='%s')"
                                .formatted(page.url(), page.title()),
                        testId,
                        TestActionType.NAVIGATED,
                        chaosScenario.id(),
                        currentStepNumber.get()
                ));

                for (int i = 0; i < stepsToExecute.size(); i++) {
                    ChSStepDto step = stepsToExecute.get(i);
                    currentStepNumber.set(step.stepNumber());
                    executedSteps.add(step);

                    publishEvent(new MonkeyTestActionDto(
                            "INFO [STEP %d/%d] %s | id=%s role=%s | value=%s"
                                    .formatted(
                                            step.stepNumber(),
                                            totalSteps,
                                            step.action(),
                                            step.targetElement().id(),
                                            step.targetElement().role(),
                                            step.fillValue()
                                    ),
                            testId,
                            TestActionType.STEP_STARTED,
                            chaosScenario.id(),
                            currentStepNumber.get()
                    ));

                    AriaRole role = this.getAriaRole(step.targetElement().role());

                    Locator locator = this.locatorService.locateElement(
                            step,
                            page,
                            role,
                            totalSteps,
                            errors
                    );

                    this.actService.actElement(
                            step,
                            locator,
                            totalSteps,
                            role,
                            errors,
                            chaosScenario.id(),
                            testId,
                            currentStepNumber.get()
                    );

                    Optional<DecidedScenarioDto> decidedScenario = this.decideNextStep(
                            browserSettings,
                            pageDto,
                            currentPageAnalyze,
                            testId,
                            chaosScenario.id(),
                            currentStepNumber.get(),
                            appIdeaAnalysis,
                            auType,
                            chaosScenario
                    );

                    if(decidedScenario.isPresent()) {
                        DecidedScenarioDto decidedScenarioDto = decidedScenario.get();
                        AnalyzedAppPageDto newAnalyzedPage = decidedScenarioDto.analyzedAppPage();
                        currentPageAnalyze = new AnalyzedAppPageDto(
                            newAnalyzedPage.metadata(),
                            newAnalyzedPage.elementsCount(),
                            newAnalyzedPage.elementsCountByRoles(),
                            newAnalyzedPage.elements()
                        );

                        if(decidedScenarioDto.chSIntermediateStep().cutScenario()) {
                            stepsToExecute.clear();
                            stepsToExecute.addAll(executedSteps);
                            stepsToExecute.add(decidedScenarioDto.chSIntermediateStep().step());
                        } else {
                            int insertIndex = executedSteps.size();

                            ChSStepDto newStep = decidedScenarioDto.chSIntermediateStep().step();

                            stepsToExecute.add(insertIndex, newStep);

                            for (int j = insertIndex; j < stepsToExecute.size(); j++) {
                                ChSStepDto oldStep = stepsToExecute.get(j);

                                stepsToExecute.set(
                                        j,
                                        new ChSStepDto(
                                                j + 1,
                                                oldStep.purpose(),
                                                oldStep.action(),
                                                oldStep.fillValue(),
                                                oldStep.targetElement()
                                        )
                                );
                            }
                        }
                    }

                }

                page.waitForTimeout(2000);

                publishEvent(new MonkeyTestActionDto(
                        "INFO [SCENARIO] FINISHED id=%s steps=%d/%d consoleErrors=%d total=%d ms | final url=%s"
                                .formatted(
                                        chaosScenario.id(),
                                        currentStepNumber.get(),
                                        totalSteps,
                                        errors.size(),
                                        System.currentTimeMillis() - scenarioStart,
                                        page.url()
                                ),
                        testId,
                        TestActionType.FINISHED,
                        chaosScenario.id(),
                        currentStepNumber.get()
                ));

                this.errorCollectorService.addTestErrors(
                    testId,
                        new MonkeyTestErrorDto(
                                errors,
                                chaosScenario.id()
                        )
                );
            }
        } catch (Exception e) {
            String reason = e.getMessage();

            errors.add(
                    new TestErrorDto(
                            ErrorType.PLAYWRIGHT,
                            currentStepNumber.get(),
                            "ERROR [SCENARIO] FAILED at step %d/%d| reason: %s"
                                    .formatted(
                                            currentStepNumber.get(),
                                            totalSteps,
                                            reason
                                    )
                    )
            );

            eventDispatcher.publish(
                    new SystemEventDto<>(
                            RunMonkeyTestService.class,
                            null,
                            "ERROR [SCENARIO] FAILED at step %d/%d after %d ms | reason: %s"
                                    .formatted(
                                            currentStepNumber.get(),
                                            totalSteps,
                                            System.currentTimeMillis() - scenarioStart,
                                            reason
                                    )
                    )
            );

            this.errorCollectorService.addTestErrors(
                    testId,
                    new MonkeyTestErrorDto(
                            errors,
                            chaosScenario.id()
                    )
            );
        }
    }

    private Optional<DecidedScenarioDto> decideNextStep(
            BrowserSettings browserSettings,
            PageDto pageDto,
            AnalyzedAppPageDto prevAnalyze,
            UUID testId,
            UUID scenarioId,
            int currentStepNumber,
            AnalyzedAppIdeaDto appIdeaAnalysis,
            AuTypeDto auType,
            ChaosScenarioDto prevScenario
    ) {
        publishEvent(new MonkeyTestActionDto(
                "INFO [SCENARIO]  DECIDING NEXT STEP id=%s steps=%d"
                        .formatted(
                                scenarioId,
                                currentStepNumber
                        ),
                testId,
                TestActionType.DECIDING_NEXT_STEP,
                scenarioId,
                currentStepNumber
        ));

        AnalyzedAppPageDto newAnalyzedPage = this.analyzeBrowserDiscoveryService.analyze(pageDto, browserSettings);

        publishEvent(
                null,
                "[SCENARIO] Browser discovery completed. Scenario: %s, step: %d."
                        .formatted(scenarioId, currentStepNumber)
        );

        boolean elementsCountChanged = !prevAnalyze.elementsCount().equals(newAnalyzedPage.elementsCount());

        Map<Role, Integer> changedElementsCountByRoles = new HashMap<>();

        for(Map.Entry<Role, Integer> entry : prevAnalyze.elementsCountByRoles().entrySet()) {
            Role role = entry.getKey();
            Integer count = entry.getValue();

            Integer newAnalyzeElementCountByRole = newAnalyzedPage.elementsCountByRoles().get(role);
            if(!newAnalyzeElementCountByRole.equals(count)) {
                changedElementsCountByRoles.put(role, newAnalyzeElementCountByRole - count);
            }
        }

        publishEvent(
                null,
                "[SCENARIO] Page comparison statistics. Scenario: %s, step: %d. " +
                        "Total elements changed: %s, changed roles: %s."
                                .formatted(
                                        scenarioId,
                                        currentStepNumber,
                                        elementsCountChanged,
                                        changedElementsCountByRoles
                                )
        );


        if(elementsCountChanged || !changedElementsCountByRoles.isEmpty()) {
            publishEvent(
                    null,
                    "[SCENARIO] Page changes detected. Generating intermediate scenario step. " +
                            "Scenario: %s, step: %d."
                                    .formatted(scenarioId, currentStepNumber)
            );

            ComparedPagesDto comparedPagesDto = this.compareAnalyzedPagesService.compare(
                    prevAnalyze,
                    newAnalyzedPage,
                    changedElementsCountByRoles
            );

            publishEvent(
                    null,
                    "[SCENARIO] Page comparison completed. Scenario: %s, step: %d."
                            .formatted(scenarioId, currentStepNumber)
            );


            publishEvent(new MonkeyTestActionDto(
                    "INFO [SCENARIO] DECIDED NEW NEXT STEP id=%s steps=%d"
                            .formatted(
                                    scenarioId,
                                    currentStepNumber
                            ),
                    testId,
                    TestActionType.DECIDED_NEW_NEXT_STEP,
                    scenarioId,
                    currentStepNumber
            ));

            publishEvent(
                    null,
                    "[SCENARIO] Intermediate step generated successfully. Scenario: %s, step: %d."
                            .formatted(scenarioId, currentStepNumber)
            );

            return Optional.of(new DecidedScenarioDto(
                    newAnalyzedPage,
                    this.generateScenarioIntermediateStepService.generate(
                            appIdeaAnalysis,
                            prevAnalyze,
                            comparedPagesDto,
                            auType,
                            prevScenario,
                            currentStepNumber
                    )
            ));

        }

        publishEvent(new MonkeyTestActionDto(
                "INFO [SCENARIO]  NEW NEXT STEP CANCELED id=%s steps=%d"
                        .formatted(
                                scenarioId,
                                currentStepNumber
                        ),
                testId,
                TestActionType.NEW_NEXT_STEP_CANCELED,
                scenarioId,
                currentStepNumber
        ));

        return Optional.empty();
    }

    private AriaRole getAriaRole(Role role) {
        return switch (role) {
            case BUTTON -> AriaRole.BUTTON;
            case LINK -> AriaRole.LINK;
            case TEXTBOX -> AriaRole.TEXTBOX;
            case HEADING -> AriaRole.HEADING;
            case CHECKBOX -> AriaRole.CHECKBOX;
            case RADIO -> AriaRole.RADIO;
            case SWITCH -> AriaRole.SWITCH;
            case COMBOBOX -> AriaRole.COMBOBOX;
            case OPTION -> AriaRole.OPTION;
            case SPINBUTTON -> AriaRole.SPINBUTTON;
            case PARAGRAPH -> throw new IllegalArgumentException(
                    "PARAGRAPH does not have a corresponding AriaRole"
            );
        };
    }

    private void publishEvent(MonkeyTestActionDto monkeyTestActionDto) {
        eventDispatcher.publish(
                new SystemEventDto<>(
                        RunMonkeyTestService.class,
                        monkeyTestActionDto,
                        monkeyTestActionDto.message()
                )
        );
    }

    private <T> void publishEvent(T data, String message) {
        eventDispatcher.publish(
                new SystemEventDto<>(
                        RunMonkeyTestService.class,
                        data,
                        message
                )
        );
    }
}