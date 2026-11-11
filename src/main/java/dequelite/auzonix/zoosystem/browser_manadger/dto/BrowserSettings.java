package dequelite.auzonix.zoosystem.browser_manadger.dto;

import java.util.List;

public record BrowserSettings(
        List<Cookie> cookies,
        List<Header> headers
) {
}
