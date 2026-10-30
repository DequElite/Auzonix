package dequelite.auzonix.zoosystem.scenario_manadger.generating;

import dequelite.auzonix.zoosystem.ai.GenerateChaosScenarioPrompt;
import dequelite.auzonix.zoosystem.app_analyzer.analyzers.dto.AnalyzedPagesDto;
import dequelite.auzonix.zoosystem.app_analyzer.analyzers.dto.AnalyzedTasksDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppIdeaDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppPageDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppTaskDto;
import dequelite.auzonix.zoosystem.au_manadger.au_types.FirstAuLevelType;
import dequelite.auzonix.zoosystem.au_manadger.au_types.SecondAuLevelType;
import dequelite.auzonix.zoosystem.au_manadger.au_types.dto.AuTypeDto;
import dequelite.auzonix.zoosystem.event.disparcher.EventDispatcher;
import dequelite.auzonix.zoosystem.event.dto.SystemEventDto;
import dequelite.auzonix.zoosystem.scenario_manadger.dto.ChaosScenarioDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
            AnalyzedPagesDto analyzedAppPageDtos,
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
                                        analyzedAppPageDtos,
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
            AnalyzedPagesDto analyzedPagesDocuments,
            AnalyzedAppIdeaDto appIdeaAnalysis,
            AnalyzedTasksDto appTasksAnalysis,
            AuTypeDto auType
    ) {
        AnalyzedAppPageDto mainAnalyzedPage = analyzedPagesDocuments.pages().isEmpty()
                ? null
                : analyzedPagesDocuments.pages().getFirst();
        if(mainAnalyzedPage == null) throw new RuntimeException("analyzedPagesDocuments is null on GenerateChaosScenarioService - generate");

        return this.generateChaosScenarioPrompt.analyze(
                appIdeaAnalysis,
                appTasksAnalysis.tasks(),
                mainAnalyzedPage,
                auType.id(),
                auType.name(),
                auType.domainKnowledge(),
                auType.profileBehavior(),
                auType.rules(),
                auType.completionRule()
        );
    }
}
