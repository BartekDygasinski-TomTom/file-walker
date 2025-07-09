package pl.bdygasinski.filewalker.model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.isNull;

record FileEntry(Path value, int depthLevel) implements Entry {

    private static final String EXPECTED_A_FILE_BUT_GOT_A_DIRECTORY = "Expected a file, but got a directory: ";

    public FileEntry {
        if (isNull(value)) {
            throw new IllegalArgumentException(VALUE_MUST_NOT_BE_NULL);
        }
        if (Files.isDirectory(value)) {
            throw new IllegalArgumentException(EXPECTED_A_FILE_BUT_GOT_A_DIRECTORY + value);
        }
    }

    static FileEntry withDefaultDepthLevel(Path path) {
        return new FileEntry(path, 0);
    }

    @Override
    public Set<Entry> getRootLevelEntries() {
        return Set.of(this);
    }

    @Override
    public Set<Entry> getVisibleRootLevelEntries() {
        return this.isVisible() ? Set.of(this) : Set.of();
    }

    @Override
    public DisplayName displayName() {
        return addExtension(Entry.super.displayName().name());
    }

    private DisplayName addExtension(String fileBaseName) {
        String extension = getExtension(fileBaseName).toLowerCase();
        String label = EXTENSION_LABELS.get(extension);

        if (label != null) {
            return DisplayName.withNameAndDepthLevel("(%s) %s".formatted(label, fileBaseName), depthLevel);
        }

        return DisplayName.withNameAndDepthLevel(fileBaseName, depthLevel);
    }

    private static String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex >= 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
    }

    private static final Map<String, String> EXTENSION_LABELS = Map.ofEntries(
            Map.entry("png", "image"),
            Map.entry("jpg", "image"),
            Map.entry("bmp", "image"),
            Map.entry("sh", "script"),
            Map.entry("bat", "script"),
            Map.entry("ps1", "script"),
            Map.entry("csv", "data"),
            Map.entry("json", "data"),
            Map.entry("java", "code"),
            Map.entry("js", "code"),
            Map.entry("py", "code"),
            Map.entry("txt", "text")
    );

}