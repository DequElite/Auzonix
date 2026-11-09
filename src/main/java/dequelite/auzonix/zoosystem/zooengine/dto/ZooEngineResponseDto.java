package dequelite.auzonix.zoosystem.zooengine.dto;

import dequelite.auzonix.zoosystem.au_manadger.monkey_test.run.dto.ConsoleErrorDto;
import dequelite.auzonix.zoosystem.error_collector.dto.ErrorsSnapshotDto;
import dequelite.auzonix.zoosystem.scenario_manadger.dto.ChaosScenarioDto;

import java.util.List;

public record ZooEngineResponseDto(
    List<ChaosScenarioDto> scenarios,
    ErrorsSnapshotDto errors
) {
}
