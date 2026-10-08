package dequelite.auzonix.zoosystem.app_analyzer.dto;

import java.util.List;

public record AnalyzedAppTasksDto(
        String taskName,
        String taskDescription,
        String taskPurpose,
        List<String> requiredActions
) {
}
