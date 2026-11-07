
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
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        long startTime = System.nanoTime();
        log("INFO", step, totalSteps,
                "Starting element search. Role=%s, URL=%s"
                        .formatted(role, page.url()));

        try {
            Locator locator = ariaLocator(step, page, role, totalSteps);

            if (locator != null) {
                log("INFO", step, totalSteps,
                        "Element successfully located using ARIA strategy.");

                return locator;
            }

            log("DEBUG", step, totalSteps,
                    "ARIA strategy failed. Switching to attributes strategy.");

            locator = attributesLocator(step, page, totalSteps);

            if (locator != null) {
                log("INFO", step, totalSteps,
                        "Element successfully located using attributes strategy.");

                return locator;
            }

            log("ERROR", step, totalSteps,
                    "All locator strategies failed. Element not found.");

            throw new RuntimeException("LOCATOR NOT FOUND");

        } catch (RuntimeException exception) {
            log("ERROR", step, totalSteps,
                    "Element search failed: %s: %s"
                            .formatted(
                                    exception.getClass().getSimpleName(),
                                    exception.getMessage()
                            ));

            throw exception;

        } finally {
            long duration = (System.nanoTime() - startTime) / 1_000_000;

            log("DEBUG", step, totalSteps,
                    "Locator search finished. Duration=%d ms".formatted(duration));
        }
    }

    private Locator attributesLocator(
            ChSStepDto step,
            Page page,
            int totalSteps
    ) {
        AttributesDto attributes = step.targetElement().attributes();

        log("DEBUG", step, totalSteps,
                "Starting attributes locator. id=%s, className=%s, placeholder=%s"
                        .formatted(
                                attributes.id(),
                                attributes.className(),
                                attributes.placeholder()
                        ));

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
            log("WARN", step, totalSteps,
                    "Attributes locator skipped. No usable attributes provided.");

            return null;
        }

        String selector = String.join("", selectors);

        log("DEBUG", step, totalSteps,
                "Generated CSS selector: %s".formatted(selector));

        Locator locator = page.locator(selector);

        int count = locator.count();

        log("DEBUG", step, totalSteps,
                "Attributes selector matched %d element(s).".formatted(count));

        if (count == 1) {
            return locator;
        }

        if (count == 0) {
            log("WARN", step, totalSteps,
                    "Attributes locator found no elements.");
        } else {
            log("WARN", step, totalSteps,
                    "Attributes locator is ambiguous. Matches=%d".formatted(count));
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

        log("DEBUG", step, totalSteps,
                "Starting ARIA locator. Role=%s".formatted(role));

        log("DEBUG", step, totalSteps,
                "ARIA name candidates: content=%s, ariaLabel=%s, name=%s"
                        .formatted(
                                attributes.content(),
                                attributes.ariaLabel(),
                                attributes.name()
                        ));


        Locator locator = page.getByRole(
                role,
                new Page.GetByRoleOptions()
                        .setName(attributes.content())
        );

        int count = locator.count();

        log("DEBUG", step, totalSteps,
                "ARIA locator matched %d element(s).".formatted(count));

        if (count == 1) {
            return locator;
        }

        if (count == 0) {
            log("WARN", step, totalSteps,
                    "ARIA locator found no elements.");
        } else {
            log("WARN", step, totalSteps,
                    "ARIA locator is ambiguous. Matches=%d".formatted(count));
        }

        return null;
    }

    private void log(
            String level,
            ChSStepDto step,
            int totalSteps,
            String message
    ) {
        publishEvent(
                "%s [LocatorService | STEP %d/%d]: %s"
                        .formatted(level, step.stepNumber(), totalSteps, message)
        );
    }

    private void publishEvent(String message) {
        eventDispatcher.publish(
                new SystemEventDto<>(
                        LocatorService.class,
                        null,
                        message
                )
        );
    }
}
