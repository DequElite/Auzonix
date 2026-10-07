package dequelite.auzonix.zoosystem.app_analyzer.analyzers;

import dequelite.auzonix.zoosystem.ai.AnalyzeAppIdeaPrompt;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppIdeaDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalyzeAppIdeaService {

    private final AnalyzeAppIdeaPrompt analyzeAppIdeaPrompt;

    public AnalyzedAppIdeaDto analyze(String appDocument) {
        AnalyzedAppIdeaDto result = this.analyzeAppIdeaPrompt.analyze(appDocument);


        return result;
    }
}
