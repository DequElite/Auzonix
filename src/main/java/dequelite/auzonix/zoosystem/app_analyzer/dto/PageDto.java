package dequelite.auzonix.zoosystem.app_analyzer.dto;

import jakarta.validation.constraints.NotBlank;

public record PageDto(
        @NotBlank(message = "Page name must not be blank")
        String name,

        @NotBlank(message = "Page description must not be blank")
        String description,

        @NotBlank(message = "Page URL must not be blank")
        String url
) {}