package dequelite.auzonix.api.test_manadger.start_test.dto;

import dequelite.auzonix.zoosystem.app_analyzer.dto.PageDto;
import dequelite.auzonix.zoosystem.browser_manadger.dto.BrowserSettings;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.util.List;

@Getter
public class StartTestRequestDto {

    @NotBlank(message = "App document must not be blank")
    private String appDocument;

    @NotNull(message = "Pages documents must not be null")
    @NotEmpty(message = "Pages documents must contain at least one document")
    private List<
            @NotNull(message = "Page document must not be null")
            @Valid PageDto
            > pagesDocuments;

    @NotNull(message = "AU count must not be null")
    @Positive(message = "AU count must be greater than zero")
    private Integer auCount;

    private BrowserSettings browserSettings;
}