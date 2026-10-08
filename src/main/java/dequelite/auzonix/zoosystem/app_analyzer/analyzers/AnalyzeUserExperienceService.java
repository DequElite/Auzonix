package dequelite.auzonix.zoosystem.app_analyzer.analyzers;

import dequelite.auzonix.zoosystem.ai.AnalyzeAppPagePrompt;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppIdeaDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppPageDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppTasksDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyzeUserExperienceService {
    private final AnalyzeAppPagePrompt analyzeAppPagePrompt;

    public List<AnalyzedAppPageDto> analyze(
            List<String> pagesDocuments,
            AnalyzedAppIdeaDto appIdeaAnalysis,
            List<AnalyzedAppTasksDto> appTasksAnalysis
    ) {
        return pagesDocuments.stream()
                .map(p -> this.analyzeAppPagePrompt.analyze(
                        p,
                        appIdeaAnalysis,
                        appTasksAnalysis
                ))
                .toList();
    }
}
