package pl.bdygasinski.filewalker.view;

import java.util.Map;

public class FileTypeClassifier {

    private static final Map<String, String> extensionToTypeMap = Map.ofEntries(
            // Image types
            Map.entry("png", "image"),
            Map.entry("jpg", "image"),
            Map.entry("jpeg", "image"),
            Map.entry("bmp", "image"),

            // Script types
            Map.entry("sh", "script"),
            Map.entry("bat", "script"),
            Map.entry("ps1", "script"),

            // Data types
            Map.entry("csv", "data"),
            Map.entry("json", "data"),

            // Code types
            Map.entry("java", "code"),
            Map.entry("js", "code"),
            Map.entry("py", "code"),

            // Text types
            Map.entry("txt", "text")
    );

    public static String classifyExtension(String extension) {
        if (extension == null) return "unknown";
        return extensionToTypeMap.getOrDefault(extension.toLowerCase(), "unknown");
    }
}