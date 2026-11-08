package dequelite.auzonix.zoosystem.error_collector.dto;

public record SystemErrorDto<T>(
        Class<?> sourceClass,
        String message,
        Exception exception
) {
}
