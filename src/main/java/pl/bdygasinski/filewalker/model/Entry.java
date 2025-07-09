package pl.bdygasinski.filewalker.model;

import pl.bdygasinski.filewalker.exception.EntryNotAccessibleException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Supplier;


public sealed interface Entry permits DirEntry, ErrorEntry, FileEntry {

    String VALUE_MUST_NOT_BE_NULL = "Value must not be null";


    Set<Entry> getRootLevelEntries();

    Set<Entry> getVisibleRootLevelEntries();

    default DisplayName displayName() {
        String entryName = value().getFileName().toString();
        return DisplayName.withNameAndDepthLevel(entryName, depthLevel());
    }

    default boolean isVisible() {
        try {
            return !Files.isHidden(value());

        } catch (IOException e) {
            throw new EntryNotAccessibleException("Can't access visibility data", e);
        }
    }

    Path value();

    int depthLevel();

    default Set<Entry> getVisibleEntriesRecursively(int maxDepth) {
        return getVisibleRootLevelEntries();
    }



    static Entry fromPath(Path path) {
        return fromPathWithDepthLevel(path, 0);
    }

    static Entry fromPathWithDepthLevel(Path path, int depthLevel) {
        Queue<Supplier<Entry>> entriesSupplier = new ArrayDeque<>(List.of(
                () -> new FileEntry(path, depthLevel),
                () -> new DirEntry(path, depthLevel),
                () -> new ErrorEntry(depthLevel)
        ));

        while (!entriesSupplier.isEmpty()) {
            try {
                return entriesSupplier.poll().get();

            } catch (IllegalArgumentException e) {
                // Ignore
            }
        }

        return ErrorEntry.withDefaultDepthLevel();
    }
}
