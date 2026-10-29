package dequelite.auzonix.zoosystem.zooengine;

import dequelite.auzonix.zoosystem.app_analyzer.analyzers.AnalyzeAppIdeaService;
import dequelite.auzonix.zoosystem.app_analyzer.analyzers.AnalyzeAppTasksService;
import dequelite.auzonix.zoosystem.app_analyzer.analyzers.AnalyzeUserExperienceService;
import dequelite.auzonix.zoosystem.app_analyzer.analyzers.dto.AnalyzedPagesDto;
import dequelite.auzonix.zoosystem.app_analyzer.analyzers.dto.AnalyzedTasksDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppIdeaDto;
import dequelite.auzonix.zoosystem.au_manadger.au_types.FirstAuLevelType;
import dequelite.auzonix.zoosystem.au_manadger.au_types.SecondAuLevelType;
import dequelite.auzonix.zoosystem.au_manadger.au_types.dto.AuTypeDto;
import dequelite.auzonix.zoosystem.au_manadger.monkey_test.run.RunMonkeyTestService;
import dequelite.auzonix.zoosystem.au_manadger.monkey_test.run.RunMonkeyTestsAsyncService;
import dequelite.auzonix.zoosystem.au_manadger.monkey_test.run.dto.ConsoleErrorDto;
import dequelite.auzonix.zoosystem.scenario_manadger.dto.ChaosScenarioDto;
import dequelite.auzonix.zoosystem.scenario_manadger.generating.GenerateChaosScenarioService;
import dequelite.auzonix.zoosystem.zooengine.dto.ScenarioTestResultDto;
import dequelite.auzonix.zoosystem.zooengine.dto.ZooEngineResponseDto;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class ZooEngineService {

    private final AnalyzeAppIdeaService analyzeAppIdeaService;
    private final AnalyzeAppTasksService analyzeAppTasksService;
    private final AnalyzeUserExperienceService analyzeUserExperienceService;

    private final GenerateChaosScenarioService generateChaosScenarioService;
    private final RunMonkeyTestsAsyncService runMonkeyTestsAsyncService;

    public ZooEngineResponseDto start(
            String appDocument,
            List<String> pagesDocuments,
            Integer auCount
    ) {
        log.debug("======== START ========");

        AnalyzedAppIdeaDto analyzedAppIdeaDto = analyzeAppIdea(appDocument);

        AnalyzedTasksDto analyzedAppTasksDto = analyzeAppTasks(
                appDocument,
                analyzedAppIdeaDto
        );

        AnalyzedPagesDto analyzedAppPageDtos = analyzeUserExperience(
                pagesDocuments,
                analyzedAppIdeaDto,
                analyzedAppTasksDto
        );

        List<ChaosScenarioDto> scenarios = generateChaosScenarios(
                analyzedAppPageDtos,
                analyzedAppIdeaDto,
                analyzedAppTasksDto,
                auCount
        );

        List<ScenarioTestResultDto> scenarioErrors =
                this.runMonkeyTest(scenarios);

        log.debug("======== FINISH ========");

        return new ZooEngineResponseDto(
                scenarios,
                scenarioErrors
        );
    }

    private AnalyzedAppIdeaDto analyzeAppIdea(String appDocument) {
        log.debug("//Launching first step...");

        AnalyzedAppIdeaDto result = analyzeAppIdeaService.analyze(appDocument);

        log.debug("//First step completed:");
        log.debug("//AnalyzedAppIdeaDto: {}", result);

        return result;
    }

    private AnalyzedTasksDto analyzeAppTasks(
            String appDocument,
            AnalyzedAppIdeaDto analyzedAppIdeaDto
    ) {
        log.debug("//Launching second step...");

        AnalyzedTasksDto result = analyzeAppTasksService.analyze(
                appDocument,
                analyzedAppIdeaDto
        );

        log.debug("//Second step completed:");
        log.debug("//AnalyzedAppTasksDto: {}", result);

        return result;
    }

    private AnalyzedPagesDto analyzeUserExperience(
            List<String> pagesDocuments,
            AnalyzedAppIdeaDto analyzedAppIdeaDto,
            AnalyzedTasksDto analyzedAppTasksDto
    ) {
        log.debug("//Launching third step...");

        AnalyzedPagesDto result = analyzeUserExperienceService.analyze(
                pagesDocuments,
                analyzedAppIdeaDto,
                analyzedAppTasksDto
        );

        log.debug("//Third step completed:");
        log.debug("//AnalyzedPagesDto: {}", result);

        return result;
    }

    private List<ChaosScenarioDto> generateChaosScenarios(
            AnalyzedPagesDto analyzedAppPageDtos,
            AnalyzedAppIdeaDto analyzedAppIdeaDto,
            AnalyzedTasksDto analyzedAppTasksDto,
            Integer auCount
    ) {
        log.debug("//Launching fourth step...");

        List<ChaosScenarioDto> scenarios = this.generateChaosScenarioService.generateChaosScenariosAsync(
                analyzedAppPageDtos,
                analyzedAppIdeaDto,
                analyzedAppTasksDto,
                auCount
        );

        log.debug(
                "//Fourth step completed. Generated {} scenarios",
                scenarios.size()
        );

        return scenarios;
    }

    private List<ScenarioTestResultDto> runMonkeyTest(
            List<ChaosScenarioDto> chaosScenarios
    ) {
        log.debug("//Launching fifth step...");

        List<ScenarioTestResultDto> scenariosResults = this.runMonkeyTestsAsyncService.runMonkeyTestsAsync(chaosScenarios);

        log.debug(
                "//Fifth step completed. Executed {} scenarios",
                scenariosResults.size()
        );

        return scenariosResults;
    }
}