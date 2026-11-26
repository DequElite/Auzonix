package dequelite.auzonix.zoosystem.scenario_manadger.generating;

import dequelite.auzonix.zoosystem.ai.GenerateChaosScenarioPrompt;
import dequelite.auzonix.zoosystem.app_analyzer.ai_analyzers.dto.AnalyzedPagesDto;
import dequelite.auzonix.zoosystem.app_analyzer.ai_analyzers.dto.AnalyzedTasksDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppIdeaDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppPageDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.PageElementDto;
import dequelite.auzonix.zoosystem.au_manadger.au_types.FirstAuLevelType;
import dequelite.auzonix.zoosystem.au_manadger.au_types.SecondAuLevelType;
import dequelite.auzonix.zoosystem.au_manadger.au_types.dto.AuTypeDto;
import dequelite.auzonix.zoosystem.event.disparcher.EventDispatcher;
import dequelite.auzonix.zoosystem.event.dto.SystemEventDto;
import dequelite.auzonix.zoosystem.scenario_manadger.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenerateChaosScenarioService {
    private final GenerateChaosScenarioPrompt generateChaosScenarioPrompt;

    private final SecondAuLevelType secondAuLevelType;
    private final FirstAuLevelType firstAuLevelType;

    private final Random random = new Random();

    private final EventDispatcher eventDispatcher;

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public List<ChaosScenarioDto> generateChaosScenariosAsync(
            AnalyzedAppPageDto analyzedAppPage,
            AnalyzedAppIdeaDto analyzedAppIdeaDto,
            AnalyzedTasksDto analyzedAppTasksDto,
            Integer auCount
    ) {
        List<AuTypeDto> auTypes = List.of(
                firstAuLevelType.getAuType(),
                secondAuLevelType.getAuType()
        );

        List<AuTypeDto> auTypesMap = new ArrayList<>(auCount);

        for (int i = 0; i < auCount; i++) {
            int randomIndex = this.random.nextInt(auTypes.size());

            auTypesMap.add(auTypes.get(randomIndex));
        }

        eventDispatcher.publish(
                new SystemEventDto<>(
                        GenerateChaosScenarioService.class,
                        null,
                        "Selected AU types: %s".formatted(auTypesMap)
                )
        );

        List<CompletableFuture<ChaosScenarioDto>> futures = auTypesMap.stream()
                .map(auType ->
                            CompletableFuture.supplyAsync(() -> {
                                ChaosScenarioDto scenario = this.generateChaosScenario(
                                        analyzedAppPage,
                                        analyzedAppIdeaDto,
                                        analyzedAppTasksDto,
                                        auType
                                );

                                return scenario;
                            }, executorService)
                        ).toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[0])).join();

        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    public ChaosScenarioDto generateChaosScenario(
            AnalyzedAppPageDto analyzedPage,
            AnalyzedAppIdeaDto appIdeaAnalysis,
            AnalyzedTasksDto appTasksAnalysis,
            AuTypeDto auType
    ) {
        RawChSDto rawScenario = this.generateChaosScenarioPrompt.analyze(
                appIdeaAnalysis,
                appTasksAnalysis.tasks(),
                analyzedPage,
                auType.id(),
                auType.name(),
                auType.domainKnowledge(),
                auType.profileBehavior(),
                auType.rules(),
                auType.completionRule()
        );

        Map<String, PageElementDto> pageElements = analyzedPage.elements().stream()
                .collect(Collectors.toMap(
                        PageElementDto::id,
                        Function.identity()
                ));

        List<ChSStepDto> chsSteps = rawScenario.steps().stream()
                .map(rawChSS -> {
                    PageElementDto foundEl = pageElements.get(rawChSS.targetElement().id());

                    return new ChSStepDto(
                            rawChSS.stepNumber(),
                            rawChSS.purpose(),
                            rawChSS.action(),
                            rawChSS.fillValue(),
                            new ChSTargetElementDto(
                                    rawChSS.targetElement().id(),
                                    foundEl.role(),
                                    foundEl.attributes()
                            )
                    );
                })
                .toList();

        return new ChaosScenarioDto(
                UUID.randomUUID(),
                rawScenario.name(),
                rawScenario.goal(),
                rawScenario.auType(),
                rawScenario.targetUrl(),
                chsSteps
        );
    }
}
