package pl.bdygasinski.filewalker;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toSet;


public sealed interface Entry {

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



    record FileEntry(Path value) implements Entry {

        private static final String EXPECTED_A_FILE_BUT_GOT_A_DIRECTORY = "Expected a file, but got a directory: ";

        public FileEntry {
            if (isNull(value)) {
                throw new IllegalArgumentException(VALUE_MUST_NOT_BE_NULL);
            }
            if (Files.isDirectory(value)) {
                throw new IllegalArgumentException(EXPECTED_A_FILE_BUT_GOT_A_DIRECTORY + value);
            }
        }

        @Override
        public String displayName() {
            return value.getFileName().toString();
        }

        @Override
        public Set<Entry> getAllRootLevelOrThrow() {
            return Set.of(this);
        }

        @Override
        public Set<Entry> getVisibleRootLevelOrThrow() throws IOException {
            return this.isVisible() ? Set.of(this) : Set.of();
        }

        @Override
        public boolean isVisible() throws UncheckedIOException {
            try {
                return !Files.isHidden(value);

            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }



    record DirEntry(Path value) implements Entry {

        private static final String EXPECTED_A_DIRECTORY_BUT_GOT_A_FILE = "Expected a directory, but got a file: ";

        public DirEntry {
            if (isNull(value)) {
                throw new IllegalArgumentException(VALUE_MUST_NOT_BE_NULL);
            }
            if (!Files.isDirectory(value)) {
                throw new IllegalArgumentException(EXPECTED_A_DIRECTORY_BUT_GOT_A_FILE + value);
            }
        }

        @Override
        public String displayName() {
            return String.format("[dir] %s", value.getFileName().toString());
        }

        @Override
        public Set<Entry> getAllRootLevelOrThrow() throws IOException {
            try (var dirStream = Files.list(value)) {
                return dirStream
                        .map(Entry::fromPathOrThrow)
                        .collect(toSet());
            }
        }

        @Override
        public Set<Entry> getVisibleRootLevelOrThrow() throws IOException {
            try (var dirStream = Files.list(value)) {
                return dirStream
                        .map(Entry::fromPathOrThrow)
                        .filter(Entry::isVisible)
                        .collect(toSet());
            }
        }

        @Override
        public boolean isVisible()  {
            try {
                return !Files.isHidden(value);

            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
