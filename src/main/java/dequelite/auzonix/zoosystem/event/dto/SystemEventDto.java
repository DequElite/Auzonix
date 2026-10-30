package dequelite.auzonix.zoosystem.event.dto;

import java.time.LocalDateTime;

public record SystemEventDto<T>(
        Class<?> sourceClass,
        T data,
        String message
) {
}
