package dequelite.auzonix.zoosystem.app_analyzer.browser_analyzers;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import dequelite.auzonix.zoosystem.app_analyzer.ai_analyzers.dto.AnalyzedPagesDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.*;
import dequelite.auzonix.zoosystem.error_collector.ErrorCollectorService;
import dequelite.auzonix.zoosystem.error_collector.dto.SystemErrorDto;
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
    private final ErrorCollectorService errorCollectorService;

    private final Map<Role, AriaRole> rolesToFind = Map.ofEntries(
            Map.entry(Role.BUTTON, AriaRole.BUTTON),
            Map.entry(Role.LINK, AriaRole.LINK),
            Map.entry(Role.TEXTBOX, AriaRole.TEXTBOX),
            Map.entry(Role.HEADING, AriaRole.HEADING),
            Map.entry(Role.PARAGRAPH, AriaRole.PARAGRAPH),
            Map.entry(Role.CHECKBOX, AriaRole.CHECKBOX),
            Map.entry(Role.RADIO, AriaRole.RADIO),
            Map.entry(Role.SWITCH, AriaRole.SWITCH),
            Map.entry(Role.COMBOBOX, AriaRole.COMBOBOX),
            Map.entry(Role.OPTION, AriaRole.OPTION),
            Map.entry(Role.SPINBUTTON, AriaRole.SPINBUTTON)
    );

    private final Map<Role, List<ActionType>> availableActions = Map.of(
            Role.BUTTON, List.of(ActionType.CLICK),
            Role.LINK, List.of(ActionType.CLICK),
            Role.TEXTBOX, List.of(ActionType.FILL),
            Role.SPINBUTTON, List.of(ActionType.FILL),
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
            String reason = e.getMessage();

            this.errorCollectorService.addSystemError(
                    new SystemErrorDto(
                            AnalyzeBrowserDiscoveryService.class,
                            "ERROR [SCENARIO] FAILED at Browser Discovery| reason: %s"
                                    .formatted(
                                            reason
                                    ),
                            e
                    )
            );

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
                                            element.getAttribute("placeholder"),
                                            element.getAttribute("name"),
                                            element.getAttribute("aria-label")
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
