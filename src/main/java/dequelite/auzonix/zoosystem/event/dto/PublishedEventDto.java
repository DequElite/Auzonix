package dequelite.auzonix.zoosystem.event.dto;

import java.time.LocalDateTime;

public record PublishedEventDto<T>(
        Class<?> sourceClass,
        T data,
        String message,
        LocalDateTime time
) {
}
