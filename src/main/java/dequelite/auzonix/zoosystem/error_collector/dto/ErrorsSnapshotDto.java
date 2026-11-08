package dequelite.auzonix.zoosystem.error_collector.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record ErrorsSnapshotDto(
        List<SystemErrorDto> systemErrors,
        Map<UUID, MonkeyTestErrorDto> monkeyTestsErrors
) {
}
