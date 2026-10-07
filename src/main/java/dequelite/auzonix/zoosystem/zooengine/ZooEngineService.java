package dequelite.auzonix.zoosystem.zooengine;

import dequelite.auzonix.zoosystem.app_analyzer.analyzers.AnalyzeAppIdeaService;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppIdeaDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ZooEngineService {

    private final AnalyzeAppIdeaService analyzeAppIdeaService;

    public void start(String appDocument, List<String> pagesDocuments) {
        log.debug("======== START ========");
        log.debug("//Launching first step...");

        AnalyzedAppIdeaDto analyzedAppIdeaDto = this.analyzeAppIdeaService.analyze(appDocument);
        log.debug("//First step completed:");
        log.debug("//AnalyzedAppIdeaDto: {}", analyzedAppIdeaDto.toString());

        log.debug("======== FINISH ========");
    }
}
