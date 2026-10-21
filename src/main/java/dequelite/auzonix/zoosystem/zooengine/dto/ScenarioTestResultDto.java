package dequelite.auzonix.zoosystem.zooengine.dto;

import dequelite.auzonix.zoosystem.au_manadger.monkey_test.run.dto.ConsoleErrorDto;

import java.util.List;

public record ScenarioTestResultDto(
        String scenarioId,
        List<ConsoleErrorDto> consoleErrors
) {
}
