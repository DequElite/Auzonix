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

        try (BrowserContext browserContext = this.browser.newContext()){
            Page page = browserContext.newPage();

            AtomicInteger currentStepNumber = new AtomicInteger(0);

            page.onConsoleMessage(msg -> {
                if(msg.type().equals("error")) {
                    ConsoleErrorDto consoleError = new ConsoleErrorDto(
                            currentStepNumber.get(),
                            msg.text()
                    );

                    consoleErrors.add(consoleError);
                }
            });

            page.navigate(chaosScenario.targetUrl());

            for (ChSStepDto step : chaosScenario.steps()) {
                currentStepNumber.set(step.stepNumber());

                AriaRole role = this.getAriaRole(step.targetElement().role());

                Locator locator = page.getByRole(
                        role,
                        new Page.GetByRoleOptions()
                                .setName(step.targetElement().content())
                );

                switch (step.action()) {
                    case CLICK -> locator.click();
                    case FILL -> {
                        if(role == AriaRole.TEXTBOX) {
                            locator.fill(step.fillValue());
                        } else {
                            log.error("Error during monkey testing: CANNOT FILL NOT TEXTBOX");
                            return consoleErrors;
                        }
                    }
                    default -> log.error("Error during monkey testing: ACTION IS NOT FOUND");
                }
            }

            return consoleErrors;
        } catch (Exception e) {
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
