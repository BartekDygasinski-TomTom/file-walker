package pl.bdygasinski.filewalker;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static java.util.Objects.isNull;

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
        public Set<Entry> getVisibleRootLevelOrThrow() {
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