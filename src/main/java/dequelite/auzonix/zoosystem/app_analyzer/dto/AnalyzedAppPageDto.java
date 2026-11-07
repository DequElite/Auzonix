package dequelite.auzonix.zoosystem.app_analyzer.dto;

import java.util.List;

public record AnalyzedAppPageDto(
        PageDto metadata,
        List<PageElementDto> elements
) {

}
