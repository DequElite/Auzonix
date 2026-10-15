package dequelite.auzonix.zoosystem.au_manadger.monkey_test.run;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import dequelite.auzonix.zoosystem.app_analyzer.dto.Role;
import dequelite.auzonix.zoosystem.au_manadger.monkey_test.run.dto.ConsoleErrorDto;
import dequelite.auzonix.zoosystem.scenario_manadger.dto.ChSStepDto;
import dequelite.auzonix.zoosystem.scenario_manadger.dto.ChaosScenarioDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class RunMonkeyTestService {
    private final Browser browser;

    public List<ConsoleErrorDto> run(ChaosScenarioDto chaosScenario) {
        List<ConsoleErrorDto> consoleErrors = new ArrayList<>();
        AtomicInteger currentStepNumber = new AtomicInteger(0); // [LOG] вынесено выше try, чтобы видеть шаг в catch
        int totalSteps = chaosScenario.steps().size();
        long scenarioStart = System.currentTimeMillis();

        log.info("[SCENARIO] START id={} name='{}' auType={} steps={}",
                chaosScenario.id(), chaosScenario.name(), chaosScenario.auType(), totalSteps);
        log.info("[SCENARIO] goal: {}", chaosScenario.goal());
        log.info("[SCENARIO] url: {}", chaosScenario.targetUrl());

        try (BrowserContext browserContext = this.browser.newContext()){
            Page page = browserContext.newPage();

            page.onConsoleMessage(msg -> {
                if(msg.type().equals("error")) {
                    ConsoleErrorDto consoleError = new ConsoleErrorDto(
                            currentStepNumber.get(),
                            msg.text()
                    );

                    consoleErrors.add(consoleError);
                    log.warn("[BROWSER step={}] console.error: {}", currentStepNumber.get(), msg.text());
                } else {
                    log.debug("[BROWSER step={}] console.{}: {}", currentStepNumber.get(), msg.type(), msg.text());
                }
            });

            // [LOG] только логирование событий браузера
            page.onPageError(err ->
                    log.warn("[BROWSER step={}] uncaught page error: {}", currentStepNumber.get(), err));
            page.onResponse(resp -> {
                if (resp.status() >= 400) {
                    log.warn("[HTTP step={}] {} {} -> {}", currentStepNumber.get(),
                            resp.request().method(), resp.url(), resp.status());
                }
            });
            page.onFrameNavigated(frame -> {
                if (frame == page.mainFrame()) {
                    log.info("[NAV step={}] navigated to {}", currentStepNumber.get(), frame.url());
                }
            });

            log.info("[NAV] opening {}", chaosScenario.targetUrl());
            page.navigate(chaosScenario.targetUrl());
            log.info("[NAV] opened {} (title='{}')", page.url(), page.title());

            for (ChSStepDto step : chaosScenario.steps()) {
                currentStepNumber.set(step.stepNumber());
                long stepStart = System.currentTimeMillis();

                log.info("[STEP {}/{}] {} | id={} role={} content='{}' | value={}",
                        step.stepNumber(), totalSteps, step.action(),
                        step.targetElement().id(), step.targetElement().role(),
                        step.targetElement().content(),
                        step.targetElement().content(), step.fillValue());
                log.info("[STEP {}/{}] purpose: {}", step.stepNumber(), totalSteps, step.purpose());

                AriaRole role = this.getAriaRole(step.targetElement().role());

                Locator locator;

                if(role == AriaRole.TEXTBOX) {
                    locator = page.getByPlaceholder(
                            step.targetElement().content()
                    );
                    log.debug("[STEP {}/{}] locator: getByPlaceholder('{}')",
                            step.stepNumber(), totalSteps, step.targetElement().content());
                } else {
                    locator = page.getByRole(
                            role,
                            new Page.GetByRoleOptions()
                                    .setName(step.targetElement().content())
                    );
                    log.debug("[STEP {}/{}] locator: getByRole({}, name='{}')",
                            step.stepNumber(), totalSteps, role, step.targetElement().content());
                }

                switch (step.action()) {
                    case CLICK -> {
                        log.debug("[STEP {}/{}] clicking", step.stepNumber(), totalSteps);
                        locator.click();
                        log.debug("[STEP {}/{}] clicked", step.stepNumber(), totalSteps);
                    }
                    case FILL -> {
                        if(role == AriaRole.TEXTBOX) {
                            log.debug("[STEP {}/{}] filling: {}", step.stepNumber(), totalSteps, step.fillValue());
                            locator.fill(step.fillValue());
                            log.debug("[STEP {}/{}] filled", step.stepNumber(), totalSteps);
                        } else {
                            log.error("[STEP {}/{}] CANNOT FILL NOT TEXTBOX (role={})",
                                    step.stepNumber(), totalSteps, role);
                            log.error("Error during monkey testing: CANNOT FILL NOT TEXTBOX");
                            log.info("[SCENARIO] STOPPED at step {}/{} after {} ms",
                                    step.stepNumber(), totalSteps, System.currentTimeMillis() - scenarioStart);
                            return consoleErrors;
                        }
                    }
                    default -> {
                        log.error("[STEP {}/{}] ACTION IS NOT FOUND: {}",
                                step.stepNumber(), totalSteps, step.action());
                        log.error("Error during monkey testing: ACTION IS NOT FOUND");
                    }
                }

                log.info("[STEP {}/{}] finished in {} ms | url={}",
                        step.stepNumber(), totalSteps,
                        System.currentTimeMillis() - stepStart, page.url());
            }

            log.info("[SCENARIO] FINISHED id={} steps={}/{} consoleErrors={} total={} ms | final url={}",
                    chaosScenario.id(), currentStepNumber.get(), totalSteps,
                    consoleErrors.size(), System.currentTimeMillis() - scenarioStart, page.url());
            return consoleErrors;
        } catch (Exception e) {
            log.error("[SCENARIO] FAILED at step {}/{} after {} ms | reason: {}",
                    currentStepNumber.get(), totalSteps,
                    System.currentTimeMillis() - scenarioStart,
                    e.getMessage() == null ? "unknown" : e.getMessage().lines().findFirst().orElse("unknown"));
            log.error("Error during monkey testing: ", e);
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
}