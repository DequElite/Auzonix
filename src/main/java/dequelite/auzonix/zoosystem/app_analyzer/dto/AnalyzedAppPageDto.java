package dequelite.auzonix.zoosystem.app_analyzer.dto;

import java.util.List;
import java.util.Map;

public record AnalyzedAppPageDto(
        PageDto metadata,
        Integer elementsCount,
        Map<Role, Integer> elementsCountByRoles,
        List<PageElementDto> elements
) {

}
