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
    public Set<Entry> getRootLevelEntries() {
        try (var dirStream = Files.list(value)) {
            return dirStream
                    .map(Entry::fromPath)
                    .collect(toSet());

        } catch (IOException e) {
            return Set.of(new ErrorEntry());
        }
    }

    @Override
    public Set<Entry> getVisibleRootLevelEntries() {
        if (this.isVisible()) {
            return getVisible();
        } else {
            return Set.of();
        }
    }

    @Override
    public String displayName() {
        return String.format("[dir] %s", Entry.super.displayName());
    }

    private Set<Entry> getVisible() {
        try (var dirStream = Files.list(value)) {
            return dirStream
                    .map(Entry::fromPath)
                    .filter(Entry::isVisible)
                    .collect(toSet());

        } catch (IOException e) {
            return Set.of(new ErrorEntry());
        }
    }
}