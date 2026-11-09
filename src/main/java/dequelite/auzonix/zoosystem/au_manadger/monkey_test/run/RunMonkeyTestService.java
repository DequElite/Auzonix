package dequelite.auzonix.zoosystem.au_manadger.monkey_test.run;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import dequelite.auzonix.zoosystem.app_analyzer.browser_analyzers.AnalyzeBrowserDiscoveryService;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AttributesDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.Role;
import dequelite.auzonix.zoosystem.au_manadger.monkey_test.run.dto.ConsoleErrorDto;
import dequelite.auzonix.zoosystem.error_collector.ErrorCollectorService;
import dequelite.auzonix.zoosystem.error_collector.dto.ErrorType;
import dequelite.auzonix.zoosystem.error_collector.dto.MonkeyTestErrorDto;
import dequelite.auzonix.zoosystem.error_collector.dto.SystemErrorDto;
import dequelite.auzonix.zoosystem.error_collector.dto.TestErrorDto;
import dequelite.auzonix.zoosystem.event.disparcher.EventDispatcher;
import dequelite.auzonix.zoosystem.event.dto.SystemEventDto;
import dequelite.auzonix.zoosystem.scenario_manadger.dto.ChSStepDto;
import dequelite.auzonix.zoosystem.scenario_manadger.dto.ChaosScenarioDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class RunMonkeyTestService {

    private final EventDispatcher eventDispatcher;

    private final LocatorService locatorService;
    private final ActService actService;
    private final BrowserEventListenerService browserEventListenerService;

    private final ErrorCollectorService errorCollectorService;

    public void run(ChaosScenarioDto chaosScenario) {
        List<TestErrorDto> errors = new ArrayList<>();
        UUID testId = UUID.randomUUID();

        AtomicInteger currentStepNumber = new AtomicInteger(0);
        int totalSteps = chaosScenario.steps().size();
        long scenarioStart = System.currentTimeMillis();

        publishEvent(null, "INFO [SCENARIO] START id=%s name='%s' auType=%s steps=%d"
                .formatted(
                        chaosScenario.id(),
                        chaosScenario.name(),
                        chaosScenario.auType(),
                        totalSteps
                ));

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(false)
            );

            try (BrowserContext browserContext = browser.newContext()) {
                try (Page page = browserContext.newPage()) {
                    this.browserEventListenerService.register(
                            errors,
                            page,
                            currentStepNumber
                    );

                    page.navigate(chaosScenario.targetUrl());

                    publishEvent(null, "INFO [NAV] opened %s (title='%s')"
                            .formatted(page.url(), page.title()));

                    for (ChSStepDto step : chaosScenario.steps()) {
                        currentStepNumber.set(step.stepNumber());

                        publishEvent(null, "INFO [STEP %d/%d] %s | id=%s role=%s | value=%s"
                                .formatted(
                                        step.stepNumber(),
                                        totalSteps,
                                        step.action(),
                                        step.targetElement().id(),
                                        step.targetElement().role(),
                                        step.fillValue()
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
                                errors
                        );

                    }

                    page.waitForTimeout(2000);

                    publishEvent(null, "INFO [SCENARIO] FINISHED id=%s steps=%d/%d consoleErrors=%d total=%d ms | final url=%s"
                            .formatted(
                                    chaosScenario.id(),
                                    currentStepNumber.get(),
                                    totalSteps,
                                    errors.size(),
                                    System.currentTimeMillis() - scenarioStart,
                                    page.url()
                            ));

                    this.errorCollectorService.addTestErrors(
                        testId,
                            new MonkeyTestErrorDto(
                                    errors,
                                    chaosScenario.id()
                            )
                    );
                }
            }
        } catch (Exception e) {
            String reason = e.getMessage();

            errors.add(
                    new TestErrorDto(
                            ErrorType.PLAYWRIGHT,
                            currentStepNumber.get(),
                            e,
                            "ERROR [SCENARIO] FAILED at step %d/%d| reason: %s"
                                    .formatted(
                                            currentStepNumber.get(),
                                            totalSteps,
                                            reason
                                    )
                    )
            );

            publishEvent(null, "ERROR [SCENARIO] FAILED at step %d/%d after %d ms | reason: %s"
                    .formatted(
                            currentStepNumber.get(),
                            totalSteps,
                            System.currentTimeMillis() - scenarioStart,
                            reason
                    ));

            this.errorCollectorService.addTestErrors(
                    testId,
                    new MonkeyTestErrorDto(
                            errors,
                            chaosScenario.id()
                    )
            );
        }
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