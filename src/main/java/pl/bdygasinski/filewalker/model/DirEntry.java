package pl.bdygasinski.filewalker.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toSet;

record DirEntry(Path value) implements Entry {

    private static final String EXPECTED_A_DIRECTORY_BUT_GOT_A_FILE = "Expected a directory, but got a file: ";

    DirEntry {
        if (isNull(value)) {
            throw new IllegalArgumentException(VALUE_MUST_NOT_BE_NULL);
        }
        if (!Files.isDirectory(value)) {
            throw new IllegalArgumentException(EXPECTED_A_DIRECTORY_BUT_GOT_A_FILE + value);
        }
    }

    @Override
    public String displayName() {
        return String.format("[dir] %s", Entry.super.displayName());
    }

    @Override
    public Set<Entry> getAllRootLevelOrThrow() throws IOException {
        try (var dirStream = Files.list(value)) {
            return dirStream
                    .map(Entry::fromPathOrThrow)
                    .collect(toSet());

        } catch (IllegalArgumentException e) {
            throw new IOException(e);
        }
    }

    @Override
    public Set<Entry> getVisibleRootLevelOrThrow() throws IOException {
        if (this.isVisible()) {
            return getVisible();
        } else {
            return Set.of();
        }
    }

    private Set<Entry> getVisible() throws IOException {
        try (var dirStream = Files.list(value)) {
            return dirStream
                    .map(Entry::fromPathOrThrow)
                    .filter(Entry::isVisible)
                    .collect(toSet());

        } catch (IllegalArgumentException e) {
            throw new IOException(e);
        }
    }
}