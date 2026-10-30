package dequelite.auzonix.zoosystem.au_manadger.monkey_test.run;

import dequelite.auzonix.zoosystem.au_manadger.monkey_test.run.dto.ConsoleErrorDto;
import dequelite.auzonix.zoosystem.event.disparcher.EventDispatcher;
import dequelite.auzonix.zoosystem.event.dto.SystemEventDto;
import dequelite.auzonix.zoosystem.scenario_manadger.dto.ChaosScenarioDto;
import dequelite.auzonix.zoosystem.zooengine.dto.ScenarioTestResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RunMonkeyTestsAsyncService {

    private final RunMonkeyTestService runMonkeyTestService;

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    private final EventDispatcher eventDispatcher;

    public List<ScenarioTestResultDto> runMonkeyTestsAsync(
            List<ChaosScenarioDto> chaosScenarios
    ) {
        List<CompletableFuture<ScenarioTestResultDto>> futures = chaosScenarios
                .stream()
                .map(scenario ->
                        CompletableFuture.supplyAsync(() -> {
                            eventDispatcher.publish(
                                    new SystemEventDto<>(
                                            RunMonkeyTestsAsyncService.class,
                                            null,
                                            "Running scenario: %s".formatted(scenario.id())
                                    )
                            );

                            List<ConsoleErrorDto> consoleErrors = this.runMonkeyTestService.run(
                                    scenario
                            );

                            eventDispatcher.publish(
                                    new SystemEventDto<>(
                                            RunMonkeyTestsAsyncService.class,
                                            null,
                                            "Scenario %s completed with %s errors".formatted(scenario.id(), consoleErrors.size())
                                    )
                            );

                            return  new ScenarioTestResultDto(
                                    scenario.id(),
                                    consoleErrors
                            );
                        }, executorService)
                ).toList();

        CompletableFuture.allOf(
                futures.toArray(new CompletableFuture<?>[0])
        ).join();

        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }
}
