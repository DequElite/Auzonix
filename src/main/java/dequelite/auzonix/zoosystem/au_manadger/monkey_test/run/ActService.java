
package dequelite.auzonix.zoosystem.au_manadger.monkey_test.run;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;
import dequelite.auzonix.zoosystem.error_collector.dto.ErrorType;
import dequelite.auzonix.zoosystem.error_collector.dto.TestErrorDto;
import dequelite.auzonix.zoosystem.event.disparcher.EventDispatcher;
import dequelite.auzonix.zoosystem.event.dto.SystemEventDto;
import dequelite.auzonix.zoosystem.scenario_manadger.dto.ChSStepDto;
import dequelite.auzonix.zoosystem.test_action_collector.dto.MonkeyTestActionDto;
import dequelite.auzonix.zoosystem.test_action_collector.dto.TestActionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActService {
    private final EventDispatcher eventDispatcher;

    public void actElement(ChSStepDto step, Locator locator, int totalSteps, AriaRole role, List<TestErrorDto> errors, UUID scenarioId, UUID testId, Integer currentStepNumber) {
        try {
            switch (step.action()) {
                case CLICK -> {
                    locator.click();

                    publishEvent(new MonkeyTestActionDto(
                            "DEBUG [STEP %d/%d] clicked"
                                    .formatted(step.stepNumber(), totalSteps),
                            testId,
                            TestActionType.ACTION_ON_ELEMENT,
                            scenarioId,
                            step,
                            currentStepNumber
                    ));
                }

                case CHECK -> {
                    locator.check();

                    publishEvent(new MonkeyTestActionDto(
                            "DEBUG [STEP %d/%d] checked"
                                    .formatted(step.stepNumber(), totalSteps),
                            testId,
                            TestActionType.ACTION_ON_ELEMENT,
                            scenarioId,
                            step,
                            currentStepNumber
                    ));
                }

                case UNCHECK -> {
                    locator.uncheck();

                    publishEvent(new MonkeyTestActionDto(
                            "DEBUG [STEP %d/%d] unchecked"
                                    .formatted(step.stepNumber(), totalSteps),
                            testId,
                            TestActionType.ACTION_ON_ELEMENT,
                            scenarioId,
                            step,
                            currentStepNumber
                    ));
                }

                case SELECTOPTION -> {
                    locator.selectOption(step.fillValue());

                    publishEvent(new MonkeyTestActionDto(
                            "DEBUG [STEP %d/%d] selected option"
                                    .formatted(step.stepNumber(), totalSteps),
                            testId,
                            TestActionType.ACTION_ON_ELEMENT,
                            scenarioId,
                            step,
                            currentStepNumber
                    ));
                }

                case FILL -> {
                    if (role == AriaRole.TEXTBOX || role == AriaRole.SPINBUTTON) {
                        try {
                            locator.fill(step.fillValue());
                        } catch (Exception e) {
                            publishEvent("WARN [STEP %d/%d] fill failed, trying pressSequentially"
                                    .formatted(step.stepNumber(), totalSteps));

                            locator.click();
                            locator.press("Control+A");
                            locator.pressSequentially(step.fillValue());
                        }

                        publishEvent(new MonkeyTestActionDto(
                                "DEBUG [STEP %d/%d] filled"
                                        .formatted(step.stepNumber(), totalSteps),
                                testId,
                                TestActionType.ACTION_ON_ELEMENT,
                                scenarioId,
                                step,
                                currentStepNumber
                        ));
                    } else {
                        publishEvent("ERROR [STEP %d/%d] CANNOT FILL NOT TEXTBOX (role=%s)"
                                .formatted(step.stepNumber(), totalSteps, role));

                        throw new IllegalArgumentException("ILLEGAL ROLE");
                    }
                }

                default -> {
                    publishEvent("ERROR [STEP %d/%d] ACTION IS NOT FOUND: %s"
                            .formatted(step.stepNumber(), totalSteps, step.action()));

                    throw new RuntimeException("UNKNOWN ACTION");
                }
            }
        } catch (Exception e) {
            errors.add(
                    new TestErrorDto(
                            ErrorType.PLAYWRIGHT,
                            step.stepNumber(),
                            e,
                            "Error during act element"
                    )
            );
        }
    }

    private void publishEvent(String message) {
        eventDispatcher.publish(
                new SystemEventDto<>(
                        RunMonkeyTestService.class,
                        null,
                        message
                )
        );
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
}
