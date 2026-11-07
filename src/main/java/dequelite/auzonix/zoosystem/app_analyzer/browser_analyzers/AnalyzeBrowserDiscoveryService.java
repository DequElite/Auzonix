package dequelite.auzonix.zoosystem.app_analyzer.browser_analyzers;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import dequelite.auzonix.zoosystem.app_analyzer.ai_analyzers.dto.AnalyzedPagesDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.*;
import dequelite.auzonix.zoosystem.event.disparcher.EventDispatcher;
import dequelite.auzonix.zoosystem.event.dto.SystemEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyzeBrowserDiscoveryService {
    private final EventDispatcher eventDispatcher;

    private final Map<Role, AriaRole> rolesToFind = Map.of(
            Role.BUTTON, AriaRole.BUTTON,
            Role.LINK, AriaRole.LINK,
            Role.TEXTBOX, AriaRole.TEXTBOX,
            Role.HEADING, AriaRole.HEADING,
            Role.PARAGRAPH, AriaRole.PARAGRAPH,
            Role.CHECKBOX, AriaRole.CHECKBOX,
            Role.RADIO, AriaRole.RADIO,
            Role.SWITCH, AriaRole.SWITCH,
            Role.COMBOBOX, AriaRole.COMBOBOX,
            Role.OPTION, AriaRole.OPTION
    );

    private final Map<Role, List<ActionType>> availableActions = Map.of(
            Role.BUTTON, List.of(ActionType.CLICK),
            Role.LINK, List.of(ActionType.CLICK),
            Role.TEXTBOX, List.of(ActionType.FILL),
            Role.CHECKBOX, List.of(ActionType.CHECK, ActionType.UNCHECK),
            Role.RADIO, List.of(ActionType.CHECK),
            Role.SWITCH, List.of(ActionType.CHECK, ActionType.UNCHECK),
            Role.COMBOBOX, List.of(ActionType.SELECTOPTION),
            Role.OPTION, List.of(ActionType.SELECTOPTION)
    );

    public AnalyzedPagesDto analyze(List<PageDto> pages) {
        List<AnalyzedAppPageDto> analyzedPages = new ArrayList<>();

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(false)
            );

            try (BrowserContext browserContext = browser.newContext()) {

                for (PageDto pageDto : pages) {
                    analyzedPages.add(analyzePage(pageDto, browserContext));
                }

                publishEvent(null, "INFO Browser has been discovered: %s"
                        .formatted(analyzedPages));

                return new AnalyzedPagesDto(analyzedPages);
            }
        } catch (Exception e) {
            String reason = e.getMessage() == null
                    ? "unknown"
                    : e.getMessage().lines().findFirst().orElse("unknown");

            publishEvent(null, "ERROR [SCENARIO] FAILED at Browser Discovery| reason: %s"
                    .formatted(
                            reason
                    ));

            return new AnalyzedPagesDto(analyzedPages);
        }
    }

    private AnalyzedAppPageDto analyzePage(PageDto pageDto, BrowserContext browserContext) {
        try (Page page = browserContext.newPage()) {
            page.navigate(pageDto.url());

            List<PageElementDto> foundElements = new ArrayList<>();

            for (Map.Entry<Role, AriaRole> entry : rolesToFind.entrySet()) {
                Role role = entry.getKey();
                AriaRole ariaRole = entry.getValue();

                Locator locator = page.getByRole(ariaRole);
                int foundLocators = locator.count();

                for (int i = 0; i < foundLocators; i++) {
                    Locator element = locator.nth(i);

                    List<ActionType> actions = availableActions.get(role);

                    foundElements.add(
                            new PageElementDto(
                                    role.name().toLowerCase() + "-" + i,
                                    role,
                                    new AttributesDto(
                                            element.innerText(),
                                            element.getAttribute("id"),
                                            element.getAttribute("class"),
                                            element.getAttribute("placeholder")
                                    ),
                                    actions
                            )
                    );
                }
            }

            return new AnalyzedAppPageDto(
                    pageDto,
                    foundElements
            );
        }
    }

    private <T> void publishEvent(T data, String message) {
        eventDispatcher.publish(
                new SystemEventDto<>(
                        AnalyzeBrowserDiscoveryService.class,
                        data,
                        message
                )
        );
    }
}
