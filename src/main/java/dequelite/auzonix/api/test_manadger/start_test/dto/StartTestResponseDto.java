package dequelite.auzonix.api.test_manadger.start_test.dto;

import dequelite.auzonix.zoosystem.zooengine.dto.ZooEngineResponseDto;

public record StartTestResponseDto(
        ZooEngineResponseDto testResults
) {
}
