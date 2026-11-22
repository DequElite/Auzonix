package dequelite.auzonix.zoosystem.app_analyzer.browser_analyzers;

import dequelite.auzonix.zoosystem.app_analyzer.browser_analyzers.dto.ComparedPagesDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppPageDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.PageElementDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.Role;
import dequelite.auzonix.zoosystem.au_manadger.monkey_test.run.RunMonkeyTestService;
import dequelite.auzonix.zoosystem.event.disparcher.EventDispatcher;
import dequelite.auzonix.zoosystem.event.dto.SystemEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompareAnalyzedPagesService {
    private final EventDispatcher eventDispatcher;

    public ComparedPagesDto compare(
            AnalyzedAppPageDto prevAnalyze,
            AnalyzedAppPageDto currentAnalyze,
            Map<Role, Integer> elementsCountChangedByRoles
    ) {
        publishEvent(
                null,
                "Starting comparison of analyzed pages."
        );

        List<PageElementDto> existingElements = new ArrayList<>();
        List<PageElementDto> notExistingElements = new ArrayList<>();

        for(Map.Entry<Role, Integer> entry : elementsCountChangedByRoles.entrySet()) {
            Role role = entry.getKey();

            List<PageElementDto> prevElements = prevAnalyze.elements().stream()
                    .filter(el -> el.role().equals(role))
                    .toList();

            List<PageElementDto> currentElements = currentAnalyze.elements().stream()
                    .filter(el -> el.role().equals(role))
                    .toList();

            publishEvent(
                    null,
                    "Comparing elements with role %s. Previous: %d, current: %d."
                            .formatted(role, prevElements.size(), currentElements.size())
            );

            Set<PageElementDto> currentElementsSet = new HashSet<>(currentElements);

            for(PageElementDto prevEl : prevElements) {
               if(currentElementsSet.contains(prevEl)) {
                    existingElements.add(prevEl);
                } else {
                    notExistingElements.add(prevEl);
                }
            }
        }

        Set<String> existingElementsIds = existingElements.stream()
                .map(PageElementDto::id)
                .collect(Collectors.toSet());

        List<PageElementDto> newElements = currentAnalyze.elements().stream()
                .filter(currentEl -> !existingElementsIds.contains(currentEl.id()))
                .toList();

        publishEvent(
                null,
                "Page comparison completed. Existing: %d, removed: %d, new: %d."
                        .formatted(
                                existingElements.size(),
                                notExistingElements.size(),
                                newElements.size()
                        )
        );

        return new ComparedPagesDto(existingElements, notExistingElements, newElements);
    }

    private <T> void publishEvent(T data, String message) {
        eventDispatcher.publish(
                new SystemEventDto<>(
                        CompareAnalyzedPagesService.class,
                        data,
                        message
                )
        );
    }

}
