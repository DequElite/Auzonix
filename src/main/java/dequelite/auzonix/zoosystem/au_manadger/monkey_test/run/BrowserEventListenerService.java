package dequelite.auzonix.zoosystem.au_manadger.monkey_test.run;

import com.microsoft.playwright.Page;
import dequelite.auzonix.zoosystem.au_manadger.monkey_test.run.dto.ConsoleErrorDto;
import dequelite.auzonix.zoosystem.event.disparcher.EventDispatcher;
import dequelite.auzonix.zoosystem.event.dto.SystemEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class BrowserEventListenerService {
    private final EventDispatcher eventDispatcher;

    public void register(List<ConsoleErrorDto> consoleErrors, Page page, AtomicInteger currentStepNumber) {
        page.onConsoleMessage(msg -> {
            publishEvent(null, "DEBUG [BROWSER step=%d] console.%s: %s"
                    .formatted(currentStepNumber.get(), msg.type(), msg.text()));

            if (msg.type().equals("error")) {
                ConsoleErrorDto consoleError = new ConsoleErrorDto(
                        currentStepNumber.get(),
                        msg.text()
                );

                consoleErrors.add(consoleError);

                publishEvent(consoleError, "WARN [BROWSER step=%d] console.error: %s"
                        .formatted(currentStepNumber.get(), msg.text()));
            }
        });

        page.onPageError(err ->
                publishEvent(null, "WARN [BROWSER step=%d] uncaught page error: %s"
                        .formatted(currentStepNumber.get(), err)));

        page.onResponse(resp ->
                publishEvent(null, "WARN [HTTP step=%d] %s %s -> %d"
                        .formatted(
                                currentStepNumber.get(),
                                resp.request().method(),
                                resp.url(),
                                resp.status()
                        )));

        page.onFrameNavigated(frame -> {
            if (frame == page.mainFrame()) {
                publishEvent(null, "INFO [NAV step=%d] navigated to %s"
                        .formatted(currentStepNumber.get(), frame.url()));
            }
        });

        page.onRequestFailed(request -> {
            String message = "Network request failed: "
                    + request.method() + " "
                    + request.url()
                    + " | reason=" + request.failure();

            consoleErrors.add(new ConsoleErrorDto(
                    currentStepNumber.get(),
                    message
            ));
        });
    }

    private <T> void publishEvent(T data, String message) {
        eventDispatcher.publish(
                new SystemEventDto<>(
                        RunMonkeyTestService.class,
                        data,
                        message
                )
        );
    }
}
