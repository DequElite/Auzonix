package dequelite.auzonix.zoosystem.browser_manadger.dto;

import com.microsoft.playwright.*;

public record BrowserSession(
        Playwright playwright,
        Browser browser,
        BrowserContext context
) implements AutoCloseable {

    @Override
    public void close() {
        try {
            context.close();
        } finally {
            try {
                browser.close();
            } finally {
                playwright.close();
            }
        }
    }
}
