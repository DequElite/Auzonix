package dequelite.auzonix.zoosystem.app_analyzer.dto;

import java.util.List;

public record PossibleActionDto (
        String id,
        String name,
        String description,
        List<String> requiredElements
) {}