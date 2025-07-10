package pl.bdygasinski.filewalker.view;

import pl.bdygasinski.filewalker.model.DirEntry;
import pl.bdygasinski.filewalker.model.Entry;
import pl.bdygasinski.filewalker.model.ErrorEntry;
import pl.bdygasinski.filewalker.model.FileEntry;

public record DisplayableEntry(Entry entry) {
    static final String ERROR_ENTRY_DISPLAY_NAME_PREFIX = "Unaccessible entry:";
    static final String DIR_ENTRY_DISPLAY_NAME_PREFIX = "[dir]";

    public String entryName() {
        return switch (entry) {
            case FileEntry $ -> entry.baseName();
            case DirEntry $ -> "%s %s".formatted(DIR_ENTRY_DISPLAY_NAME_PREFIX, entry.baseName());
            case ErrorEntry $ -> "%s %s".formatted(ERROR_ENTRY_DISPLAY_NAME_PREFIX, entry.path());
        };
    }
}