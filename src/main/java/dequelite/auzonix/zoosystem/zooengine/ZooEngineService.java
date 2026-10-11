package dequelite.auzonix.zoosystem.zooengine;

import dequelite.auzonix.zoosystem.app_analyzer.analyzers.AnalyzeAppIdeaService;
import dequelite.auzonix.zoosystem.app_analyzer.analyzers.AnalyzeAppTasksService;
import dequelite.auzonix.zoosystem.app_analyzer.analyzers.AnalyzeUserExperienceService;
import dequelite.auzonix.zoosystem.app_analyzer.analyzers.dto.AnalyzedPagesDto;
import dequelite.auzonix.zoosystem.app_analyzer.analyzers.dto.AnalyzedTasksDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppIdeaDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppPageDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppTaskDto;
import dequelite.auzonix.zoosystem.au_manadger.au_types.SecondAuLevelType;
import dequelite.auzonix.zoosystem.au_manadger.au_types.dto.AuTypeDto;
import dequelite.auzonix.zoosystem.au_manadger.monkey_test.run.RunMonkeyTestService;
import dequelite.auzonix.zoosystem.au_manadger.monkey_test.run.dto.ConsoleErrorDto;
import dequelite.auzonix.zoosystem.scenario_manadger.dto.ChaosScenarioDto;
import dequelite.auzonix.zoosystem.scenario_manadger.generating.GenerateChaosScenarioService;
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
    private final AnalyzeUserExperienceService analyzeUserExperienceService;

    private final GenerateChaosScenarioService generateChaosScenarioService;

    private final RunMonkeyTestService runMonkeyTestService;

    private final SecondAuLevelType secondAuLevelType;

    public void start(String appDocument, List<String> pagesDocuments) {
        log.debug("======== START ========");

        log.debug("//Launching first step...");
        AnalyzedAppIdeaDto analyzedAppIdeaDto = this.analyzeAppIdeaService.analyze(appDocument);
        log.debug("//First step completed:");
        log.debug("//AnalyzedAppIdeaDto: {}", analyzedAppIdeaDto.toString());

        log.debug("//Launching second step...");
        AnalyzedTasksDto analyzedAppTasksDto = this.analyzeAppTasksService.analyze(
                appDocument, analyzedAppIdeaDto
        );
        log.debug("//Second step completed:");
        log.debug("//AnalyzedAppTasksDto: {}", analyzedAppTasksDto.toString());

        log.debug("//Launching third step...");
        AnalyzedPagesDto analyzedAppPageDtos = this.analyzeUserExperienceService.analyze(
                pagesDocuments, analyzedAppIdeaDto, analyzedAppTasksDto
        );
        log.debug("//Third step completed:");
        log.debug("//AnalyzedAppPageDto: {}", analyzedAppPageDtos.toString());

        log.debug("//Launching fourth step...");
        AuTypeDto auType = this.secondAuLevelType.getAuType();

        ChaosScenarioDto chaosScenarioDto = this.generateChaosScenarioService.generate(
                analyzedAppPageDtos,
                analyzedAppIdeaDto,
                analyzedAppTasksDto,
                auType
        );
        log.debug("//Fourth step completed:");
        log.debug("//ChaosScenarioDto: {}", chaosScenarioDto);

        log.debug("//Launching fifth step...");
        List<ConsoleErrorDto> consoleErrorDtos = this.runMonkeyTestService.run(
                chaosScenarioDto
        );
        log.debug("//Fifth step completed:");
        log.debug("//ConsoleErrorDto: {}", consoleErrorDtos);

        log.debug("======== FINISH ========");
    }
}
