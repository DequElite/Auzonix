package dequelite.auzonix.zoosystem.app_analyzer.dto;

import java.util.List;

public record PageElementDto(
        String id,
        Role role,
        String content,
        String href,
        List<ActionType> supportedActions
) {

}