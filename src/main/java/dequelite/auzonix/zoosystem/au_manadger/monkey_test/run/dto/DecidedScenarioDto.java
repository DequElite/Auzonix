package dequelite.auzonix.zoosystem.au_manadger.monkey_test.run.dto;

import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppPageDto;
import dequelite.auzonix.zoosystem.scenario_manadger.dto.ChSIntermediateStepDto;

public record DecidedScenarioDto(
        AnalyzedAppPageDto analyzedAppPage,
        ChSIntermediateStepDto chSIntermediateStep
) {
}
