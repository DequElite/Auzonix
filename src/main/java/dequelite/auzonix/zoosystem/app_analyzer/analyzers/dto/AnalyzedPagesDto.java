package dequelite.auzonix.zoosystem.app_analyzer.analyzers.dto;

import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppPageDto;

import java.util.List;

public record AnalyzedPagesDto(
        List<AnalyzedAppPageDto> pages
) {
}
