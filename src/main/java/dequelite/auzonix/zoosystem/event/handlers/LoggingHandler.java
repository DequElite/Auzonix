package dequelite.auzonix.zoosystem.event.handlers;

import dequelite.auzonix.zoosystem.event.dto.PublishedEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoggingHandler implements IHandler {

    @Override
    public void handle(PublishedEventDto<?> event) {
        if(event.data() == null) {
            log.debug(
                    "({} | {}): {}",
                    event.sourceClass().getSimpleName(),
                    event.time(),
                    event.message()
            );
        } else {
            log.debug(
                    "({} | {}): {} \n data: {}",
                    event.sourceClass().getSimpleName(),
                    event.time(),
                    event.message(),
                    event.data()
            );
        }
    }
}
