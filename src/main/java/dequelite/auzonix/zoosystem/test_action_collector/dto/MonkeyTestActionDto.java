package dequelite.auzonix.zoosystem.test_action_collector.dto;

import dequelite.auzonix.zoosystem.scenario_manadger.dto.ChSStepDto;

import java.util.UUID;

public record MonkeyTestActionDto (
        String message,
        UUID testId,
        TestActionType type,
        UUID scenarioId,
        Integer stepNumber
) {
}
