package pl.bdygasinski.filewalker.view;

import pl.bdygasinski.filewalker.model.DirEntry;
import pl.bdygasinski.filewalker.model.Entry;
import pl.bdygasinski.filewalker.model.ErrorEntry;
import pl.bdygasinski.filewalker.model.FileEntry;

public record DisplayableEntry(Entry entry) {
    private static final short SPACES_PER_ENTRY_DEPTH_LEVEL = 4;

    public String entryName() {
        if (!entry.isVisible()) {
            return "";
        }

        return switch (entry) {
            case FileEntry $ -> fileNameEntry();
            case DirEntry $ -> "[dir] %s".formatted(entry.baseName());
            case ErrorEntry $ -> entry.baseName();
        };
    }

    public String entryNameWithIndentation() {
        return indentation() + entryName();
    }

    private String indentation() {
        return " ".repeat(entry.depthLevel() * SPACES_PER_ENTRY_DEPTH_LEVEL);
    }

    private String fileNameEntry() {
        String extensionLabel = FileTypeClassifier.classifyExtension(entry.fileExtension().orElse(""));
        String humanReadableFileSize = entry.sizeInBytes()
                .map(FileSizeFormatter::toHumanReadable)
                .orElse("unknown");

        return "(%s) %s (%s)".formatted(extensionLabel, entry.baseName(), humanReadableFileSize);
    }
}
