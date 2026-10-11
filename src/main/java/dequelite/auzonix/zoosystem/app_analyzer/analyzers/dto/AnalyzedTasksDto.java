package dequelite.auzonix.zoosystem.app_analyzer.analyzers.dto;

import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppTaskDto;

import java.util.List;

public record AnalyzedTasksDto(
        List<AnalyzedAppTaskDto> tasks
) {
}
