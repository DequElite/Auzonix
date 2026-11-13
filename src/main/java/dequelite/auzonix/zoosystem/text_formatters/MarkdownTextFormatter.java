package dequelite.auzonix.zoosystem.text_formatters;

public class MarkdownTextFormatter {
    public static String of(String text) {
        if (text == null || text.isBlank()) return null;

        return text
                .replaceAll("(?m)^#{1,6}\\s*", "")
                .replaceAll("\\*{1,3}(.*?)\\*{1,3}", "$1")
                .replaceAll("_{1,3}(.*?)_{1,3}", "$1")
                .replaceAll(
                        "[\\x{1F300}-\\x{1FAFF}\\x{2600}-\\x{27BF}\\x{FE0F}\\x{200D}]",
                        ""
                )

                .replaceAll("\\s*\\R\\s*", " ")
                .replaceAll("\\s{2,}", " ")
                .trim();
    }
}
