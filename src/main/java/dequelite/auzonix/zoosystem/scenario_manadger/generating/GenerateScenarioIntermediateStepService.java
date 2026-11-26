package dequelite.auzonix.zoosystem.scenario_manadger.generating;

import dequelite.auzonix.zoosystem.ai.GenerateScenarioIntermediateStepPrompt;
import dequelite.auzonix.zoosystem.app_analyzer.browser_analyzers.dto.ComparedPagesDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppIdeaDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppPageDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.PageElementDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.Role;
import dequelite.auzonix.zoosystem.au_manadger.au_types.dto.AuTypeDto;
import dequelite.auzonix.zoosystem.scenario_manadger.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        RawChSIntermediateStepDto rawStep = this.generateScenarioIntermediateStepPrompt
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

        Map<String, PageElementDto> elementsMap = Stream.concat(
                prevAnalyze.elements().stream(),
                comparedPagesDto.newElements().stream()
        ).collect(Collectors.toMap(
                PageElementDto::id,
                Function.identity(),
                (existing, replacement) -> existing
        ));

        PageElementDto pageElement = elementsMap.get(rawStep.step().targetElement().id());

        return new ChSIntermediateStepDto(
                rawStep.cutScenario(),
                new ChSStepDto(
                        rawStep.step().stepNumber(),
                        rawStep.step().purpose(),
                        rawStep.step().action(),
                        rawStep.step().fillValue(),
                        new ChSTargetElementDto(
                                pageElement.id(),
                                pageElement.role(),
                                pageElement.attributes()
                        )
                )
        );
    }
}
