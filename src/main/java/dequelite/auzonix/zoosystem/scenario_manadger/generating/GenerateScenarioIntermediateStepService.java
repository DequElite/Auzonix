package dequelite.auzonix.zoosystem.scenario_manadger.generating;

import dequelite.auzonix.zoosystem.ai.GenerateScenarioIntermediateStepPrompt;
import dequelite.auzonix.zoosystem.app_analyzer.browser_analyzers.dto.ComparedPagesDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppIdeaDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppPageDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.Role;
import dequelite.auzonix.zoosystem.au_manadger.au_types.dto.AuTypeDto;
import dequelite.auzonix.zoosystem.scenario_manadger.dto.ChSIntermediateStepDto;
import dequelite.auzonix.zoosystem.scenario_manadger.dto.ChaosScenarioDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class GenerateScenarioIntermediateStepService {
    private final GenerateScenarioIntermediateStepPrompt generateScenarioIntermediateStepPrompt;

    public ChSIntermediateStepDto generate(
            AnalyzedAppIdeaDto appIdeaAnalysis,
            AnalyzedAppPageDto prevAnalyze,
            ComparedPagesDto comparedPagesDto,
            AuTypeDto auType,
            ChaosScenarioDto prevScenario,
            Integer currentStepNum
    ) {
        return this.generateScenarioIntermediateStepPrompt
                .generate(
                        appIdeaAnalysis,
                        prevAnalyze,
                        comparedPagesDto.notExistingElements(),
                        comparedPagesDto.newElements(),
                        prevScenario,
                        currentStepNum,
                        auType.id(),
                        auType.name(),
                        auType.domainKnowledge(),
                        auType.profileBehavior(),
                        auType.rules(),
                        auType.completionRule()
                );
    }
}
