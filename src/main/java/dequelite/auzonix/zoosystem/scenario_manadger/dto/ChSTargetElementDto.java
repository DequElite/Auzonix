package dequelite.auzonix.zoosystem.scenario_manadger.dto;

import dequelite.auzonix.zoosystem.app_analyzer.dto.Role;

public record ChSTargetElementDto(
        String id,
        Role role,
        String content
) {
}
