package dequelite.auzonix.zoosystem.app_analyzer.dto;

import java.util.List;

public record PageElementDto(
        String id,
        Role role,
        AttributesDto attributes,
        List<ActionType> supportedActions
) {

}