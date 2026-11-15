package dequelite.auzonix.infra.config;

import dequelite.auzonix.zoosystem.event.disparcher.EventDispatcher;
import dequelite.auzonix.zoosystem.event.handlers.LoggingHandler;
import dequelite.auzonix.zoosystem.event.handlers.TestActionCollectorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.Executors;

@Configuration
public class EventConfig {

    @Bean
    public EventDispatcher eventDispatcher(
            LoggingHandler loggingHandler,
            TestActionCollectorHandler testActionCollectorHandler
    ) {
        return new EventDispatcher(
                List.of(loggingHandler, testActionCollectorHandler),
                Executors.newFixedThreadPool(4)
        );
    }
}
