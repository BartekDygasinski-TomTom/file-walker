package pl.bdygasinski.filewalker.model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static java.util.Objects.isNull;

record FileEntry(Path value, int depthLevel) implements Entry {

    private static final String EXPECTED_A_FILE_BUT_GOT_A_DIRECTORY = "Expected a file, but got a directory: ";

    public FileEntry {
        if (isNull(value)) {
            throw new IllegalArgumentException(VALUE_MUST_NOT_BE_NULL);
        }
        if (Files.isDirectory(value)) {
            throw new IllegalArgumentException(EXPECTED_A_FILE_BUT_GOT_A_DIRECTORY + value);
        }
    }

    static FileEntry withDefaultDepthLevel(Path path) {
        return new FileEntry(path, 0);
    }

    @Override
    public Set<Entry> getRootLevelEntries() {
        return Set.of(this);
    }

    @Override
    public Set<Entry> getVisibleRootLevelEntries() {
        return this.isVisible() ? Set.of(this) : Set.of();
    }
}