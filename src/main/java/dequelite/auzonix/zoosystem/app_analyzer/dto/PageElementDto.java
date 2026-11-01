package dequelite.auzonix.zoosystem.app_analyzer.dto;

import java.util.List;

public record PageElementDto(
        String id,
        Role role,
        AttributesDto attributes,
        String href,
        List<ActionType> supportedActions
) {

}