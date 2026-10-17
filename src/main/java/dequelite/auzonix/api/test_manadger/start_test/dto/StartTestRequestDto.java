package dequelite.auzonix.api.test_manadger.start_test.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.List;

@Getter
public class StartTestRequestDto {
    @NotBlank(message = "App document must not be blank")
    @Size(message = "App document must not exceed 100000 characters")
    private String appDocument;

    @NotNull(message = "Pages documents must not be null")
    @NotEmpty(message = "Pages documents must contain at least one document")
    private List<
                @NotBlank(message = "Page document must not be blank")
                @Size(message = "Page document must not exceed 100000 characters")
                        String
                > pagesDocuments;
}
