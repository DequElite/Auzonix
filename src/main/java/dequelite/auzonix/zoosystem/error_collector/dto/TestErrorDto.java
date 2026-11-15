package dequelite.auzonix.zoosystem.error_collector.dto;

public record TestErrorDto(
    ErrorType type,
    Integer stepNumber,
    String message
) {
}
