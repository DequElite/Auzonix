package dequelite.auzonix.zoosystem.au_manadger.monkey_test.run;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AttributesDto;
import dequelite.auzonix.zoosystem.event.disparcher.EventDispatcher;
import dequelite.auzonix.zoosystem.event.dto.SystemEventDto;
import dequelite.auzonix.zoosystem.scenario_manadger.dto.ChSStepDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocatorService {
    private final EventDispatcher eventDispatcher;

    public Locator locateElement(
            ChSStepDto step,
            Page page,
            AriaRole role,
            int totalSteps
    ) {
        Locator locator = ariaLocator(step, page, role, totalSteps);

        if (locator != null) {
            return locator;
        }

        locator = attributesLocator(step, page, totalSteps);

        if (locator != null) {
            return locator;
        }

        throw new RuntimeException("LOCATOR NOT FOUND");
    }

    private Locator attributesLocator(
            ChSStepDto step,
            Page page,
            int totalSteps
    ) {
        AttributesDto attributes = step.targetElement().attributes();

        List<String> selectors = new ArrayList<>();

        if(!attributes.id().isEmpty())  selectors.add("[id='%s']".formatted(attributes.id()));
        if(!attributes.className().isEmpty())  selectors.add("[class='%s']".formatted(attributes.className()));
        if(!attributes.placeholder().isEmpty())  selectors.add("[placeholder='%s']".formatted(attributes.placeholder()));

        if (!selectors.isEmpty()) {
            Locator attributesLocator = page.locator(String.join("", selectors));

            if (attributesLocator.count() == 1) {
                return attributesLocator;
            }
        }

        publishEvent("DEBUG [STEP %d/%d]: selectorsLocator not found or ambiguous"
                .formatted(step.stepNumber(), totalSteps));

        return null;
    }


    private Locator ariaLocator(
            ChSStepDto step,
            Page page,
            AriaRole role,
            int totalSteps
    ) {
        Locator ariaLocator = page.getByRole(
                role,
                new Page.GetByRoleOptions()
                        .setName(step.targetElement().attributes().content())
        );

        if (ariaLocator.count() == 1) {
            return ariaLocator;
        }

        publishEvent("DEBUG [STEP %d/%d]: ariaLocator not found or ambiguous"
                .formatted(step.stepNumber(), totalSteps));

        return null;
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
}
