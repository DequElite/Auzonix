package dequelite.auzonix.zoosystem.au_manadger.monkey_test.run;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AttributesDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.Role;
import dequelite.auzonix.zoosystem.au_manadger.monkey_test.run.dto.ConsoleErrorDto;
import dequelite.auzonix.zoosystem.event.disparcher.EventDispatcher;
import dequelite.auzonix.zoosystem.event.dto.SystemEventDto;
import dequelite.auzonix.zoosystem.scenario_manadger.dto.ChSStepDto;
import dequelite.auzonix.zoosystem.scenario_manadger.dto.ChaosScenarioDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class RunMonkeyTestService {

    private final EventDispatcher eventDispatcher;

    private final LocatorService locatorService;
    private final ActService actService;
    private final BrowserEventListenerService browserEventListenerService;

    public List<ConsoleErrorDto> run(ChaosScenarioDto chaosScenario) {
        List<ConsoleErrorDto> consoleErrors = new ArrayList<>();
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
                Page page = browserContext.newPage();

                this.browserEventListenerService.register(
                        consoleErrors,
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
                            totalSteps
                    );

                   this.actService.actElement(
                           step,
                           locator,
                           totalSteps,
                           role
                   );

                }

                page.waitForTimeout(2000);

                publishEvent(null, "INFO [SCENARIO] FINISHED id=%s steps=%d/%d consoleErrors=%d total=%d ms | final url=%s"
                        .formatted(
                                chaosScenario.id(),
                                currentStepNumber.get(),
                                totalSteps,
                                consoleErrors.size(),
                                System.currentTimeMillis() - scenarioStart,
                                page.url()
                        ));

                return consoleErrors;
            }
        } catch (Exception e) {
            String reason = e.getMessage() == null
                    ? "unknown"
                    : e.getMessage().lines().findFirst().orElse("unknown");

            publishEvent(null, "ERROR [SCENARIO] FAILED at step %d/%d after %d ms | reason: %s"
                    .formatted(
                            currentStepNumber.get(),
                            totalSteps,
                            System.currentTimeMillis() - scenarioStart,
                            reason
                    ));

            return consoleErrors;
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