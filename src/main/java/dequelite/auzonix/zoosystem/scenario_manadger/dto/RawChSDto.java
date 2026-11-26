package dequelite.auzonix.zoosystem.scenario_manadger.dto;

import java.util.List;
import java.util.UUID;

public record RawChSDto (
        UUID id,
        String name,
        String goal,
        String auType,
        String targetUrl,
        List<RawChSStepDto> steps
) {

}
