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


public sealed interface Entry permits DirEntry, FileEntry {

    String CAN_T_PRODUCE_ENTRY_FROM_GIVEN_PATH = "Can't produce entry from given path: ";
    String VALUE_MUST_NOT_BE_NULL = "Value must not be null";


    Set<Entry> getAllRootLevelOrThrow() throws IOException;

    Set<Entry> getVisibleRootLevelOrThrow() throws IOException;

    default String displayName() {
        return value().getFileName().toString();
    }

    default boolean isVisible() {
        try {
            return !Files.isHidden(value());

        } catch (IOException e) {
            throw new EntryNotAccessibleException("Can't access visibility data", e);
        }
    }

    Path value();



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
