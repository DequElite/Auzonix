package dequelite.auzonix.zoosystem.zooengine;

import dequelite.auzonix.zoosystem.app_analyzer.ai_analyzers.AnalyzeAppIdeaService;
import dequelite.auzonix.zoosystem.app_analyzer.ai_analyzers.AnalyzeAppTasksService;
import dequelite.auzonix.zoosystem.app_analyzer.ai_analyzers.AnalyzeUserExperienceService;
import dequelite.auzonix.zoosystem.app_analyzer.ai_analyzers.dto.AnalyzedPagesDto;
import dequelite.auzonix.zoosystem.app_analyzer.ai_analyzers.dto.AnalyzedTasksDto;
import dequelite.auzonix.zoosystem.app_analyzer.browser_analyzers.AnalyzeBrowserDiscoveryService;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppIdeaDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.PageDto;
import dequelite.auzonix.zoosystem.au_manadger.monkey_test.run.RunMonkeyTestsAsyncService;
import dequelite.auzonix.zoosystem.event.disparcher.EventDispatcher;
import dequelite.auzonix.zoosystem.event.dto.SystemEventDto;
import dequelite.auzonix.zoosystem.scenario_manadger.dto.ChaosScenarioDto;
import dequelite.auzonix.zoosystem.scenario_manadger.generating.GenerateChaosScenarioService;
import dequelite.auzonix.zoosystem.zooengine.dto.ScenarioTestResultDto;
import dequelite.auzonix.zoosystem.zooengine.dto.ZooEngineResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ZooEngineService {

    private final AnalyzeAppIdeaService analyzeAppIdeaService;
    private final AnalyzeAppTasksService analyzeAppTasksService;
    //private final AnalyzeUserExperienceService analyzeUserExperienceService;

    private final AnalyzeBrowserDiscoveryService analyzeBrowserDiscoveryService;

    private final GenerateChaosScenarioService generateChaosScenarioService;
    private final RunMonkeyTestsAsyncService runMonkeyTestsAsyncService;

    private final EventDispatcher eventDispatcher;

    public ZooEngineResponseDto start(
            String appDocument,
            List<PageDto> pagesDocuments,
            Integer auCount
    ) {
        publishEvent(null, "======== START ========");

        AnalyzedAppIdeaDto analyzedAppIdeaDto = analyzeAppIdea(appDocument);

        AnalyzedTasksDto analyzedAppTasksDto = analyzeAppTasks(
                appDocument,
                analyzedAppIdeaDto
        );

//        AnalyzedPagesDto analyzedAppPageDtos = analyzeUserExperience(
//                pagesDocuments,
//                analyzedAppIdeaDto,
//                analyzedAppTasksDto
//        );

        AnalyzedPagesDto analyzedAppPageDtos = analyzeAppPages(
                pagesDocuments
        );

        List<ChaosScenarioDto> scenarios = generateChaosScenarios(
                analyzedAppPageDtos,
                analyzedAppIdeaDto,
                analyzedAppTasksDto,
                auCount
        );

        List<ScenarioTestResultDto> scenarioErrors =
                this.runMonkeyTest(scenarios);

        publishEvent(null, "======== FINISH ========");

        return new ZooEngineResponseDto(
                scenarios,
                scenarioErrors
        );
    }

    private AnalyzedAppIdeaDto analyzeAppIdea(String appDocument) {
        publishEvent(null, "//Launching first step...");

        AnalyzedAppIdeaDto result = analyzeAppIdeaService.analyze(appDocument);

        publishEvent(result, "//First step completed");
        //log.debug("//AnalyzedAppIdeaDto: {}", result);

        return result;
    }

    private AnalyzedTasksDto analyzeAppTasks(
            String appDocument,
            AnalyzedAppIdeaDto analyzedAppIdeaDto
    ) {
        publishEvent(null, "//Launching second step...");

        AnalyzedTasksDto result = analyzeAppTasksService.analyze(
                appDocument,
                analyzedAppIdeaDto
        );

        publishEvent(result, "//Second step completed");
        //log.debug("//AnalyzedAppTasksDto: {}", result);

        return result;
    }

//    private AnalyzedPagesDto analyzeUserExperience(
//            List<String> pagesDocuments,
//            AnalyzedAppIdeaDto analyzedAppIdeaDto,
//            AnalyzedTasksDto analyzedAppTasksDto
//    ) {
//        publishEvent(null, "//Launching third step...");
//
//        AnalyzedPagesDto result = analyzeUserExperienceService.analyze(
//                pagesDocuments,
//                analyzedAppIdeaDto,
//                analyzedAppTasksDto
//        );
//
//        publishEvent(result, "//Third step completed");
//        //log.debug("//AnalyzedPagesDto: {}", result);
//
//        return result;
//    }

    private AnalyzedPagesDto analyzeAppPages(
            List<PageDto> pages
    ) {
        publishEvent(null, "//Launching third step...");

        AnalyzedPagesDto result = this.analyzeBrowserDiscoveryService.analyze(
                pages
        );

        publishEvent(result, "//Third step completed");

        return result;
    }

    private List<ChaosScenarioDto> generateChaosScenarios(
            AnalyzedPagesDto analyzedAppPageDtos,
            AnalyzedAppIdeaDto analyzedAppIdeaDto,
            AnalyzedTasksDto analyzedAppTasksDto,
            Integer auCount
    ) {
        publishEvent(null, "//Launching fourth step...");

        List<ChaosScenarioDto> scenarios = this.generateChaosScenarioService.generateChaosScenariosAsync(
                analyzedAppPageDtos,
                analyzedAppIdeaDto,
                analyzedAppTasksDto,
                auCount
        );

        publishEvent(
                scenarios,
                "//Fourth step completed. Generated %d scenarios".formatted(scenarios.size())
        );

        return scenarios;
    }

    private List<ScenarioTestResultDto> runMonkeyTest(
            List<ChaosScenarioDto> chaosScenarios
    ) {
        publishEvent(null, "//Launching fifth step...");

        List<ScenarioTestResultDto> scenariosResults = this.runMonkeyTestsAsyncService.runMonkeyTestsAsync(chaosScenarios);

        publishEvent(
                scenariosResults,
                "//Fifth step completed. Executed %s scenarios".formatted(scenariosResults.size())
        );

        return scenariosResults;
    }

    private <T> void publishEvent(T data, String message) {
        eventDispatcher.publish(
                new SystemEventDto<>(
                        ZooEngineService.class,
                        data,
                        message
                )
        );
    }
}