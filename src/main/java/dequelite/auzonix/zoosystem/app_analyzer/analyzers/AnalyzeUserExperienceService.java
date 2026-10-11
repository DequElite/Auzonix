package dequelite.auzonix.zoosystem.app_analyzer.analyzers;

import dequelite.auzonix.zoosystem.ai.AnalyzeAppPagePrompt;
import dequelite.auzonix.zoosystem.app_analyzer.analyzers.dto.AnalyzedPagesDto;
import dequelite.auzonix.zoosystem.app_analyzer.analyzers.dto.AnalyzedTasksDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppIdeaDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppPageDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppTaskDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyzeUserExperienceService {
    private final AnalyzeAppPagePrompt analyzeAppPagePrompt;

    public AnalyzedPagesDto analyze(
            List<String> pagesDocuments,
            AnalyzedAppIdeaDto appIdeaAnalysis,
            AnalyzedTasksDto appTasksAnalysis
    ) {
        List<AnalyzedAppPageDto> analyzedAppPages = pagesDocuments.stream()
                .map(p -> this.analyzeAppPagePrompt.analyze(
                        p,
                        appIdeaAnalysis,
                        appTasksAnalysis.tasks()
                ))
                .toList();
        return new AnalyzedPagesDto(analyzedAppPages);
    }
}
