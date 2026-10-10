package dequelite.auzonix.zoosystem.scenario_manadger.dto;

import java.util.List;

public record ChaosScenarioDto(
        String id,
        String name,
        String goal,
        String auType,
        String targetUrl,
        List<ChSStepDto> steps
) {
}
