package dequelite.auzonix.zoosystem.browser_manadger.dto;

public record Cookie(
        String name,
        String value,
        String domain,
        String path,
        String sameSite,
        Boolean httpOnly,
        Boolean secure
) {
}
