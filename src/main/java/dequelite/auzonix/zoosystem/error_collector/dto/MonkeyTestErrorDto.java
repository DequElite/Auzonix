package dequelite.auzonix.zoosystem.error_collector.dto;

import java.util.List;
import java.util.UUID;

public record MonkeyTestErrorDto(
        List<TestErrorDto> errors,
        UUID scenarioId
) {
}
