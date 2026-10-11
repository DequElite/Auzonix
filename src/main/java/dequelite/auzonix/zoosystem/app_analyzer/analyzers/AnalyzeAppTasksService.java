package dequelite.auzonix.zoosystem.app_analyzer.analyzers;

import dequelite.auzonix.zoosystem.ai.AnalyzeAppTasksPrompt;
import dequelite.auzonix.zoosystem.app_analyzer.analyzers.dto.AnalyzedTasksDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppIdeaDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppTaskDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalyzeAppTasksService {

    private final AnalyzeAppTasksPrompt analyzeAppTasksPrompt;

    public AnalyzedTasksDto analyze(String appDocument, AnalyzedAppIdeaDto analyzedAppIdeaDto) {
        return this.analyzeAppTasksPrompt.analyze(appDocument, analyzedAppIdeaDto);
    }

}
