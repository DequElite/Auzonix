
package dequelite.auzonix.zoosystem.browser_manadger.factory;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.Cookie;
import com.microsoft.playwright.options.SameSiteAttribute;
import dequelite.auzonix.zoosystem.browser_manadger.dto.BrowserSession;
import dequelite.auzonix.zoosystem.browser_manadger.dto.BrowserSettings;
import dequelite.auzonix.zoosystem.browser_manadger.dto.Header;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BrowserFactory {

    public BrowserSession createSession(BrowserSettings settings) {
        Playwright playwright = Playwright.create();

        try {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(false)
            );

            try {
                BrowserContext context = browser.newContext();

                try {
                    configureContext(context, settings);

                    return new BrowserSession(playwright, browser, context);
                } catch (Exception e) {
                    context.close();
                    throw e;
                }
            } catch (Exception e) {
                browser.close();
                throw e;
            }

        } catch (Exception e) {
            playwright.close();
            throw new RuntimeException("Failed to create browser session", e);
        }
    }

    private void configureContext(
            BrowserContext context,
            BrowserSettings settings
    ) {
        if (settings.cookies() != null && !settings.cookies().isEmpty()) {
            context.addCookies(
                    settings.cookies().stream()
                            .map(cookie -> new Cookie(cookie.name(), cookie.value())
                                    .setDomain(cookie.domain())
                                    .setPath(cookie.path())
                                    .setSameSite(
                                            SameSiteAttribute.valueOf(cookie.sameSite())
                                    )
                                    .setHttpOnly(cookie.httpOnly())
                                    .setSecure(cookie.secure()))
                            .toList()
            );
        }

        Map<String, String> headers = new HashMap<>();

        if (settings.headers() != null) {
            for (Header header : settings.headers()) {
                headers.put(header.name(), header.value());
            }
        }

        if (!headers.isEmpty()) {
            context.setExtraHTTPHeaders(headers);
        }
    }
}
