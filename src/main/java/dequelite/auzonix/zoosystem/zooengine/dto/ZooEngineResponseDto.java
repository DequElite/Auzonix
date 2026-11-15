package dequelite.auzonix.zoosystem.zooengine.dto;

import dequelite.auzonix.zoosystem.au_manadger.monkey_test.run.dto.ConsoleErrorDto;
import dequelite.auzonix.zoosystem.error_collector.dto.ErrorsSnapshotDto;
import dequelite.auzonix.zoosystem.scenario_manadger.dto.ChaosScenarioDto;
import dequelite.auzonix.zoosystem.test_action_collector.dto.MonkeyTestActionDto;

import java.util.List;
import java.util.Map;

public record ZooEngineResponseDto(
    List<ChaosScenarioDto> scenarios,
    ErrorsSnapshotDto errors,
    Map<String, List<MonkeyTestActionDto>> actions
) {
}
