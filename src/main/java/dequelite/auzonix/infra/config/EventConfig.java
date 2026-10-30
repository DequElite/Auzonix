package dequelite.auzonix.infra.config;

import dequelite.auzonix.zoosystem.event.disparcher.EventDispatcher;
import dequelite.auzonix.zoosystem.event.handlers.LoggingHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.Executors;

@Configuration
public class EventConfig {

    @Bean
    public EventDispatcher eventDispatcher(
            LoggingHandler loggingHandler
    ) {
        return new EventDispatcher(
                List.of(loggingHandler),
                Executors.newFixedThreadPool(4)
        );
    }
}
