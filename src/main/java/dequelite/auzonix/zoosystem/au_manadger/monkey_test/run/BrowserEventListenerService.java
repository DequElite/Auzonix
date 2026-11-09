package dequelite.auzonix.zoosystem.au_manadger.monkey_test.run;

import com.microsoft.playwright.Page;
import dequelite.auzonix.zoosystem.au_manadger.monkey_test.run.dto.ConsoleErrorDto;
import dequelite.auzonix.zoosystem.error_collector.dto.ErrorType;
import dequelite.auzonix.zoosystem.error_collector.dto.TestErrorDto;
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

    public void register(List<TestErrorDto> errors, Page page, AtomicInteger currentStepNumber) {
        page.onConsoleMessage(msg -> {
            publishEvent(null, "DEBUG [BROWSER step=%d] console.%s: %s"
                    .formatted(currentStepNumber.get(), msg.type(), msg.text()));

            if (msg.type().equals("error")) {
                TestErrorDto errorDto = new TestErrorDto(
                    ErrorType.CONSOLE,
                    currentStepNumber.get(),
                    null,
                    msg.text()
                );

                errors.add(errorDto);

                publishEvent(errors, "WARN [BROWSER step=%d] console.error: %s"
                        .formatted(currentStepNumber.get(), msg.text()));
            }
        });

        page.onPageError(err -> {
            TestErrorDto errorDto = new TestErrorDto(
                    ErrorType.PAGE,
                    currentStepNumber.get(),
                    null,
                    err
            );

            errors.add(errorDto);

            publishEvent(null, "WARN [BROWSER step=%d] uncaught page error: %s"
                    .formatted(currentStepNumber.get(), err));
        });

        page.onRequestFailed(request -> {
            String msg = "Network request failed: "
                    + request.method() + " "
                    + request.url()
                    + " | reason=" + request.failure();

            TestErrorDto errorDto = new TestErrorDto(
                    ErrorType.HTTP,
                    currentStepNumber.get(),
                    null,
                    msg
            );

            errors.add(errorDto);

            publishEvent(null, "WARN [HTTP step=%d] request failed: %s"
                    .formatted(currentStepNumber.get(), msg));
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
