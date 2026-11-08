package dequelite.auzonix.zoosystem.error_collector;

import dequelite.auzonix.zoosystem.error_collector.dto.ErrorsSnapshotDto;
import dequelite.auzonix.zoosystem.error_collector.dto.MonkeyTestErrorDto;
import dequelite.auzonix.zoosystem.error_collector.dto.SystemErrorDto;
import dequelite.auzonix.zoosystem.event.disparcher.EventDispatcher;
import dequelite.auzonix.zoosystem.event.dto.SystemEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class ErrorCollectorService {
    private List<SystemErrorDto> systemErrors = new ArrayList<>();
    private Map<UUID, MonkeyTestErrorDto> monkeyTestsErrors = new HashMap<>();

    private final EventDispatcher eventDispatcher;

    public void addSystemError(SystemErrorDto systemErrorDto) {
        this.systemErrors.add(systemErrorDto);
        eventDispatcher.publish(
                new SystemEventDto<>(
                        systemErrorDto.sourceClass(),
                        systemErrorDto.exception(),
                        systemErrorDto.message()
                )
        );
    }

    public synchronized void addTestErrors(
            UUID testId,
            MonkeyTestErrorDto monkeyTestError
    ) {
        this.monkeyTestsErrors.put(testId, monkeyTestError);
    }

    public ErrorsSnapshotDto getErrors() {
        return new ErrorsSnapshotDto(
                this.systemErrors,
                this.monkeyTestsErrors
        );
    }
}
