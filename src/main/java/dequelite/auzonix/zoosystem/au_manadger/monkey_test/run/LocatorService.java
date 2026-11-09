
package dequelite.auzonix.zoosystem.au_manadger.monkey_test.run;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AttributesDto;
import dequelite.auzonix.zoosystem.error_collector.ErrorCollectorService;
import dequelite.auzonix.zoosystem.error_collector.dto.ErrorType;
import dequelite.auzonix.zoosystem.error_collector.dto.TestErrorDto;
import dequelite.auzonix.zoosystem.event.disparcher.EventDispatcher;
import dequelite.auzonix.zoosystem.event.dto.SystemEventDto;
import dequelite.auzonix.zoosystem.scenario_manadger.dto.ChSStepDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LocatorService {
    public Locator locateElement(
            ChSStepDto step,
            Page page,
            AriaRole role,
            int totalSteps,
            List<TestErrorDto> errors
    ) {
        try {
            Locator locator = ariaLocator(step, page, role, totalSteps);

            if (locator != null) {
                return locator;
            }

            locator = attributesLocator(step, page, totalSteps);

            if (locator != null) {
                return locator;
            }

            throw new RuntimeException("LOCATOR NOT FOUND");

        } catch (RuntimeException exception) {
            TestErrorDto errorDto = new TestErrorDto(
                    ErrorType.PLAYWRIGHT,
                    step.stepNumber(),
                    exception,
                    "Element search failed: %s: %s"
                            .formatted(
                                    exception.getClass().getSimpleName(),
                                    exception.getMessage()
                            )
            );

            errors.add(errorDto);

            throw exception;

        }
    }

    private Locator attributesLocator(
            ChSStepDto step,
            Page page,
            int totalSteps
    ) {
        AttributesDto attributes = step.targetElement().attributes();

        List<String> selectors = new ArrayList<>();

        if (attributes.id() != null && !attributes.id().isEmpty()) {
            selectors.add("[id='%s']".formatted(attributes.id()));
        }

        if (attributes.className() != null && !attributes.className().isEmpty()) {
            selectors.add("[class='%s']".formatted(attributes.className()));
        }

        if (attributes.placeholder() != null && !attributes.placeholder().isEmpty()) {
            selectors.add("[placeholder='%s']".formatted(attributes.placeholder()));
        }

        if (attributes.name() != null && !attributes.name().isEmpty()) {
            selectors.add("[name='%s']".formatted(attributes.name()));
        }

        if (attributes.ariaLabel() != null && !attributes.ariaLabel().isEmpty()) {
            selectors.add("[aria-label='%s']".formatted(attributes.ariaLabel()));
        }

        if (selectors.isEmpty()) {
            return null;
        }

        String selector = String.join("", selectors);

        Locator locator = page.locator(selector);

        int count = locator.count();

        if (count == 1) {
            return locator;
        }

        return null;
    }

    private Locator ariaLocator(
            ChSStepDto step,
            Page page,
            AriaRole role,
            int totalSteps
    ) {
        AttributesDto attributes = step.targetElement().attributes();

        Locator locator = page.getByRole(
                role,
                new Page.GetByRoleOptions()
                        .setName(attributes.content())
        );

        int count = locator.count();

        if (count == 1) {
            return locator;
        }

        return null;
    }

}
