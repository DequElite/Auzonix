package dequelite.auzonix.zoosystem.event.handlers;

import dequelite.auzonix.zoosystem.event.dto.PublishedEventDto;
import dequelite.auzonix.zoosystem.test_action_collector.TestActionCollectorService;
import dequelite.auzonix.zoosystem.test_action_collector.dto.MonkeyTestActionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestActionCollectorHandler implements IHandler {
    private final TestActionCollectorService service;

    @Override
    public void handle(PublishedEventDto<?> event) {
        if (!(event.data() instanceof MonkeyTestActionDto action)) return;
        service.addAction(action);
    }
}
