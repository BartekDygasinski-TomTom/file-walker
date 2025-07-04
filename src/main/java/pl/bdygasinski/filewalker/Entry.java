package pl.bdygasinski.filewalker;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Supplier;


public sealed interface Entry permits DirEntry, FileEntry {

    String CAN_T_PRODUCE_ENTRY_FROM_GIVEN_PATH = "Can't produce entry from given path: ";
    String VALUE_MUST_NOT_BE_NULL = "Value must not be null";


    String displayName();

    Set<Entry> getAllRootLevelOrThrow() throws IOException;

    Set<Entry> getVisibleRootLevelOrThrow() throws IOException;

    boolean isVisible() throws UncheckedIOException;



    static Entry fromPathOrThrow(Path path) {
        Queue<Supplier<Entry>> entriesSupplier = new ArrayDeque<>(List.of(
                () -> new FileEntry(path),
                () -> new DirEntry(path)
        ));

        while (!entriesSupplier.isEmpty()) {
            try {
                return entriesSupplier.poll().get();

            } catch (IllegalArgumentException e) {
                // Ignore
            }
        }

        throw new IllegalArgumentException(CAN_T_PRODUCE_ENTRY_FROM_GIVEN_PATH + path);
    }
}
