package dequelite.auzonix.zoosystem.app_analyzer.dto;

import java.util.List;

public record AnalyzedAppIdeaDto(
        String applicationType,
        String coreIdea,
        String mainPurpose,
        String applicationGoal,
        List<UserGoalsDto> userGoals
) {
}
