package dequelite.auzonix.zoosystem.event.disparcher;

import dequelite.auzonix.zoosystem.event.dto.PublishedEventDto;
import dequelite.auzonix.zoosystem.event.dto.SystemEventDto;
import dequelite.auzonix.zoosystem.event.handlers.IHandler;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.time.LocalDateTime;

public class EventDispatcher {
    private final List<IHandler> handlers;
    private final ExecutorService executorService;

    public EventDispatcher(
            List<IHandler> handlers,
            ExecutorService executorService
    ) {
        this.handlers = List.copyOf(handlers);
        this.executorService = executorService;
    }

    public void publish(SystemEventDto<?> event) {
        if (this.handlers.isEmpty()) return;

        PublishedEventDto<?> publishedEvent = new PublishedEventDto<>(
                event.sourceClass(),
                event.data(),
                event.message(),
                LocalDateTime.now()
        );

        for(IHandler handler : handlers) {
            executorService.execute(() -> handler.handle(publishedEvent));
        }
    }
}
