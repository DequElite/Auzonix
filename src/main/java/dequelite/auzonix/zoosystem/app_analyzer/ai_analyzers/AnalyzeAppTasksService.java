package dequelite.auzonix.zoosystem.app_analyzer.ai_analyzers;

import dequelite.auzonix.zoosystem.ai.AnalyzeAppTasksPrompt;
import dequelite.auzonix.zoosystem.app_analyzer.ai_analyzers.dto.AnalyzedTasksDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppIdeaDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalyzeAppTasksService {

    private final AnalyzeAppTasksPrompt analyzeAppTasksPrompt;

    public AnalyzedTasksDto analyze(String appDocument, AnalyzedAppIdeaDto analyzedAppIdeaDto) {
        return this.analyzeAppTasksPrompt.analyze(appDocument, analyzedAppIdeaDto);
    }

}
