package dequelite.auzonix.zoosystem.scenario_manadger.dto;

import dequelite.auzonix.zoosystem.app_analyzer.dto.ActionType;

public record RawChSStepDto(
        Integer stepNumber,
        String purpose,
        ActionType action,
        String fillValue,
        RawChSTargetElementDto targetElement
) {
}
