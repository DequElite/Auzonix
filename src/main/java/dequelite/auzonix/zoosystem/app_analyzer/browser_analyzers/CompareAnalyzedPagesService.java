package dequelite.auzonix.zoosystem.app_analyzer.browser_analyzers;

import dequelite.auzonix.zoosystem.app_analyzer.browser_analyzers.dto.ComparedPagesDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppPageDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.PageElementDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompareAnalyzedPagesService {
    public ComparedPagesDto compare(
            AnalyzedAppPageDto prevAnalyze,
            AnalyzedAppPageDto currentAnalyze,
            Integer elementsCountChanged,
            Map<Role, Integer> elementsCountChangedByRoles
    ) {
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


        return new ComparedPagesDto(existingElements, notExistingElements, newElements);
    }

}
