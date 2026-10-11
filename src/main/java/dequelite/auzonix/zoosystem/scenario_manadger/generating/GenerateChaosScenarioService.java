package dequelite.auzonix.zoosystem.scenario_manadger.generating;

import dequelite.auzonix.zoosystem.ai.GenerateChaosScenarioPrompt;
import dequelite.auzonix.zoosystem.app_analyzer.analyzers.dto.AnalyzedPagesDto;
import dequelite.auzonix.zoosystem.app_analyzer.analyzers.dto.AnalyzedTasksDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppIdeaDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppPageDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppTaskDto;
import dequelite.auzonix.zoosystem.au_manadger.au_types.dto.AuTypeDto;
import dequelite.auzonix.zoosystem.scenario_manadger.dto.ChaosScenarioDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenerateChaosScenarioService {
    private final GenerateChaosScenarioPrompt generateChaosScenarioPrompt;

    public ChaosScenarioDto generate(
            AnalyzedPagesDto analyzedPagesDocuments,
            AnalyzedAppIdeaDto appIdeaAnalysis,
            AnalyzedTasksDto appTasksAnalysis,
            AuTypeDto auType
    ) {
        AnalyzedAppPageDto mainAnalyzedPage = analyzedPagesDocuments.pages().isEmpty()
                ? null
                : analyzedPagesDocuments.pages().get(0);
        if(mainAnalyzedPage == null) throw new RuntimeException("analyzedPagesDocuments is null on GenerateChaosScenarioService - generate");

        return this.generateChaosScenarioPrompt.analyze(
                appIdeaAnalysis,
                appTasksAnalysis.tasks(),
                mainAnalyzedPage,
                auType.id(),
                auType.name(),
                auType.domainKnowledge()
        );
    }
}
