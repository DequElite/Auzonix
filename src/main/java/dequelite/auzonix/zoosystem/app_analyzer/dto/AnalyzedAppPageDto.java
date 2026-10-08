package dequelite.auzonix.zoosystem.app_analyzer.dto;

import java.util.List;

public record AnalyzedAppPageDto(
        String name,
        String purpose,
        String url,
        List<PageElementDto> elements,
        List<PossibleActionDto> possibleActions
) {

}
