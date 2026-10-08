package dequelite.auzonix.zoosystem.app_analyzer.analyzers;

import dequelite.auzonix.zoosystem.ai.AnalyzeAppTasksPrompt;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppIdeaDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppTasksDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalyzeAppTasksService {

    private final AnalyzeAppTasksPrompt analyzeAppTasksPrompt;

    public List<AnalyzedAppTasksDto> analyze(String appDocument, AnalyzedAppIdeaDto analyzedAppIdeaDto) {
        return this.analyzeAppTasksPrompt.analyze(appDocument, analyzedAppIdeaDto);
    }

}
