package dequelite.auzonix.zoosystem.scenario_manadger.generating;

import dequelite.auzonix.zoosystem.ai.GenerateChaosScenarioPrompt;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppIdeaDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppPageDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppTasksDto;
import dequelite.auzonix.zoosystem.au_manadger.au_types.SecondAuLevelType;
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
            List<AnalyzedAppPageDto> analyzedPagesDocuments,
            AnalyzedAppIdeaDto appIdeaAnalysis,
            List<AnalyzedAppTasksDto> appTasksAnalysis,
            AuTypeDto auType
    ) {
        AnalyzedAppPageDto mainAnalyzedPage = analyzedPagesDocuments.isEmpty()
                ? null
                : analyzedPagesDocuments.get(0);
        if(mainAnalyzedPage == null) throw new RuntimeException("analyzedPagesDocuments is null on GenerateChaosScenarioService - generate");

        return this.generateChaosScenarioPrompt.analyze(
                appIdeaAnalysis,
                appTasksAnalysis,
                mainAnalyzedPage,
                auType.id(),
                auType.name(),
                auType.domainKnowledge()
        );
    }
}
