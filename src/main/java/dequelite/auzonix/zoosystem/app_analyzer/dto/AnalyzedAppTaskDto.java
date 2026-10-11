package dequelite.auzonix.zoosystem.app_analyzer.dto;

import java.util.List;

public record AnalyzedAppTaskDto(
        String taskName,
        String taskDescription,
        String taskPurpose,
        List<String> requiredActions
) {
}
