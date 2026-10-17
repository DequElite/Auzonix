package dequelite.auzonix.api.test_manadger.start_test;

import dequelite.auzonix.api.test_manadger.start_test.dto.StartTestRequestDto;
import dequelite.auzonix.api.test_manadger.start_test.dto.StartTestResponseDto;
import dequelite.auzonix.zoosystem.zooengine.ZooEngineService;
import dequelite.auzonix.zoosystem.zooengine.dto.ZooEngineResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/test/start")
public class StartTestController {
    private final ZooEngineService zooEngineService;

    @PostMapping
    public ResponseEntity<StartTestResponseDto> start(
            @Valid @RequestBody StartTestRequestDto body
            ) {
        ZooEngineResponseDto zooEngineResponse = this.zooEngineService.start(
                body.getAppDocument(),
                body.getPagesDocuments()
        );

        StartTestResponseDto response = new StartTestResponseDto(zooEngineResponse);

        return ResponseEntity.ok(response);
    }
}
