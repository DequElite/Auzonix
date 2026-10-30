package dequelite.auzonix.zoosystem.au_manadger.monkey_test.run;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
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

        publishEvent(null, "INFO [SCENARIO] goal: " + chaosScenario.goal());
        publishEvent(null, "INFO [SCENARIO] url: " + chaosScenario.targetUrl());

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(false)
            );

            try (BrowserContext browserContext = browser.newContext()) {
                Page page = browserContext.newPage();

                page.onConsoleMessage(msg -> {
                    publishEvent(null, "DEBUG [BROWSER step=%d] console.%s: %s"
                            .formatted(currentStepNumber.get(), msg.type(), msg.text()));

                    if (msg.type().equals("error")) {
                        ConsoleErrorDto consoleError = new ConsoleErrorDto(
                                currentStepNumber.get(),
                                msg.text()
                        );

                        consoleErrors.add(consoleError);

                        publishEvent(consoleError, "WARN [BROWSER step=%d] console.error: %s"
                                .formatted(currentStepNumber.get(), msg.text()));
                    }
                });

                page.onPageError(err ->
                        publishEvent(null, "WARN [BROWSER step=%d] uncaught page error: %s"
                                .formatted(currentStepNumber.get(), err)));

                page.onResponse(resp ->
                        publishEvent(null, "WARN [HTTP step=%d] %s %s -> %d"
                                .formatted(
                                        currentStepNumber.get(),
                                        resp.request().method(),
                                        resp.url(),
                                        resp.status()
                                )));

                page.onFrameNavigated(frame -> {
                    if (frame == page.mainFrame()) {
                        publishEvent(null, "INFO [NAV step=%d] navigated to %s"
                                .formatted(currentStepNumber.get(), frame.url()));
                    }
                });

                page.onRequestFailed(request -> {
                    String message = "Network request failed: "
                            + request.method() + " "
                            + request.url()
                            + " | reason=" + request.failure();

                    publishEvent(null, "WARN [NETWORK step=%d] %s"
                            .formatted(currentStepNumber.get(), message));

                    consoleErrors.add(new ConsoleErrorDto(
                            currentStepNumber.get(),
                            message
                    ));
                });

                publishEvent(null, "INFO [NAV] opening " + chaosScenario.targetUrl());

                page.navigate(chaosScenario.targetUrl());

                publishEvent(null, "INFO [NAV] opened %s (title='%s')"
                        .formatted(page.url(), page.title()));

                for (ChSStepDto step : chaosScenario.steps()) {
                    currentStepNumber.set(step.stepNumber());
                    long stepStart = System.currentTimeMillis();

                    publishEvent(null, "INFO [STEP %d/%d] %s | id=%s role=%s content='%s' | value=%s"
                            .formatted(
                                    step.stepNumber(),
                                    totalSteps,
                                    step.action(),
                                    step.targetElement().id(),
                                    step.targetElement().role(),
                                    step.targetElement().content(),
                                    step.fillValue()
                            ));

                    publishEvent(null, "INFO [STEP %d/%d] purpose: %s"
                            .formatted(step.stepNumber(), totalSteps, step.purpose()));

                    AriaRole role = this.getAriaRole(step.targetElement().role());

                    Locator locator;

                    if (role == AriaRole.TEXTBOX) {
                        locator = page.getByPlaceholder(
                                step.targetElement().content()
                        );

                        publishEvent(null, "DEBUG [STEP %d/%d] locator: getByPlaceholder('%s')"
                                .formatted(
                                        step.stepNumber(),
                                        totalSteps,
                                        step.targetElement().content()
                                ));
                    } else {
                        locator = page.getByRole(
                                role,
                                new Page.GetByRoleOptions()
                                        .setName(step.targetElement().content())
                        );

                        publishEvent(null, "DEBUG [STEP %d/%d] locator: getByRole(%s, name='%s')"
                                .formatted(
                                        step.stepNumber(),
                                        totalSteps,
                                        role,
                                        step.targetElement().content()
                                ));
                    }

                    switch (step.action()) {
                        case CLICK -> {
                            publishEvent(null, "DEBUG [STEP %d/%d] clicking"
                                    .formatted(step.stepNumber(), totalSteps));

                            locator.click();

                            publishEvent(null, "DEBUG [STEP %d/%d] clicked"
                                    .formatted(step.stepNumber(), totalSteps));
                        }

                        case FILL -> {
                            if (role == AriaRole.TEXTBOX) {
                                publishEvent(null, "DEBUG [STEP %d/%d] filling: %s"
                                        .formatted(
                                                step.stepNumber(),
                                                totalSteps,
                                                step.fillValue()
                                        ));

                                locator.fill(step.fillValue());

                                publishEvent(null, "DEBUG [STEP %d/%d] filled"
                                        .formatted(step.stepNumber(), totalSteps));
                            } else {
                                publishEvent(null, "ERROR [STEP %d/%d] CANNOT FILL NOT TEXTBOX (role=%s)"
                                        .formatted(step.stepNumber(), totalSteps, role));

                                publishEvent(null, "ERROR Error during monkey testing: CANNOT FILL NOT TEXTBOX");

                                publishEvent(null, "INFO [SCENARIO] STOPPED at step %d/%d after %d ms"
                                        .formatted(
                                                step.stepNumber(),
                                                totalSteps,
                                                System.currentTimeMillis() - scenarioStart
                                        ));

                                return consoleErrors;
                            }
                        }

                        default -> {
                            publishEvent(null, "ERROR [STEP %d/%d] ACTION IS NOT FOUND: %s"
                                    .formatted(step.stepNumber(), totalSteps, step.action()));

                            publishEvent(null, "ERROR Error during monkey testing: ACTION IS NOT FOUND");
                        }
                    }

                    publishEvent(null, "INFO [STEP %d/%d] finished in %d ms | url=%s"
                            .formatted(
                                    step.stepNumber(),
                                    totalSteps,
                                    System.currentTimeMillis() - stepStart,
                                    page.url()
                            ));
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

            publishEvent(e, "ERROR Error during monkey testing");

            return consoleErrors;
        }
    }

    private AriaRole getAriaRole(Role role) {
        return switch (role) {
            case BUTTON -> AriaRole.BUTTON;
            case LINK -> AriaRole.LINK;
            case TEXTBOX -> AriaRole.TEXTBOX;
            case HEADING -> AriaRole.HEADING;
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