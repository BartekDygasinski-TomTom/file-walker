package pl.bdygasinski.filewalker.model;

import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Supplier;

public sealed interface Entry permits DirEntry, ErrorEntry, FileEntry {

    int depthLevel();

     boolean isVisible();

    String baseName();

    default Optional<Long> sizeInBytes() {
        return Optional.empty();
    }

    default Optional<String> fileExtension() {
        return Optional.empty();
    }

    static Entry fromPathAndGraphDepth(Path path, int graphDepth) {
        Queue<Supplier<Entry>> entriesSupplier = new ArrayDeque<>(List.of(
                () -> FileEntry.fromPathAndDepthLevel(path, graphDepth),
                () -> DirEntry.fromPathAndDepthLevel(path, graphDepth)
        ));

        while (!entriesSupplier.isEmpty()) {
            try {
                return entriesSupplier.poll().get();

            } catch (IllegalArgumentException e) {
                // Ignore
            }
        }

        return new ErrorEntry(graphDepth, path);
    }
}
