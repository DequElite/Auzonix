package dequelite.auzonix.zoosystem.au_manadger.monkey_test.run;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;
import dequelite.auzonix.zoosystem.event.disparcher.EventDispatcher;
import dequelite.auzonix.zoosystem.event.dto.SystemEventDto;
import dequelite.auzonix.zoosystem.scenario_manadger.dto.ChSStepDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActService {
    private final EventDispatcher eventDispatcher;

    public void actElement(ChSStepDto step, Locator locator, int totalSteps, AriaRole role) {
        switch (step.action()) {
            case CLICK -> {
                locator.click();

                publishEvent(null, "DEBUG [STEP %d/%d] clicked"
                        .formatted(step.stepNumber(), totalSteps));
            }

            case CHECK -> {
                locator.check();

                publishEvent(null, "DEBUG [STEP %d/%d] checked"
                        .formatted(step.stepNumber(), totalSteps));
            }

            case UNCHECK -> {
                locator.uncheck();

                publishEvent(null, "DEBUG [STEP %d/%d] unchecked"
                        .formatted(step.stepNumber(), totalSteps));
            }

            case SELECTOPTION -> {
                locator.selectOption(step.fillValue());

                publishEvent(null, "DEBUG [STEP %d/%d] selected option"
                        .formatted(step.stepNumber(), totalSteps));
            }

            case FILL -> {
                if (role == AriaRole.TEXTBOX || role == AriaRole.SPINBUTTON) {
                    try {
                        locator.fill(step.fillValue());
                    } catch (Exception e) {
                        publishEvent(null, "WARN [STEP %d/%d] fill failed, trying pressSequentially"
                                .formatted(step.stepNumber(), totalSteps));

                        locator.click();
                        locator.press("Control+A");
                        locator.pressSequentially(step.fillValue());
                    }

                    publishEvent(null, "DEBUG [STEP %d/%d] filled"
                            .formatted(step.stepNumber(), totalSteps));
                } else {
                    publishEvent(null, "ERROR [STEP %d/%d] CANNOT FILL NOT TEXTBOX (role=%s)"
                            .formatted(step.stepNumber(), totalSteps, role));

                    throw new IllegalArgumentException("ILLEGAL ROLE");
                }
            }

            default -> {
                publishEvent(null, "ERROR [STEP %d/%d] ACTION IS NOT FOUND: %s"
                        .formatted(step.stepNumber(), totalSteps, step.action()));

                throw new RuntimeException("UNKNOWN ACTION");
            }
        }
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
