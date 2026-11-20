package dequelite.auzonix.zoosystem.app_analyzer.browser_analyzers.dto;

import dequelite.auzonix.zoosystem.app_analyzer.dto.PageElementDto;

import java.util.List;

public record ComparedPagesDto(
        List<PageElementDto> existingElements,
        List<PageElementDto> notExistingElements,
        List<PageElementDto> newElements
) {
}
