package pl.bdygasinski.filewalker.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toSet;

record DirEntry(Path value, int depthLevel) implements Entry {

    private static final String EXPECTED_A_DIRECTORY_BUT_GOT_A_FILE = "Expected a directory, but got a file: ";

    DirEntry {
        if (isNull(value)) {
            throw new IllegalArgumentException(VALUE_MUST_NOT_BE_NULL);
        }
        if (!Files.isDirectory(value)) {
            throw new IllegalArgumentException(EXPECTED_A_DIRECTORY_BUT_GOT_A_FILE + value);
        }
    }

    static DirEntry withDefaultDepthLevel(Path path) {
        return new DirEntry(path, 0);
    }

    @Override
    public Set<Entry> getRootLevelEntries() {
        try (var dirStream = Files.list(value)) {
            return dirStream
                    .map(path -> Entry.fromPathWithDepthLevel(path, depthLevel() + 1))
                    .collect(Collectors.toSet());

        } catch (IOException e) {
            return Set.of(ErrorEntry.withDefaultDepthLevel());
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
    public DisplayName displayName() {
        String name = String.format("[dir] %s", Entry.super.displayName().name());
        return DisplayName.withNameAndDepthLevel(name, depthLevel);
    }

    private Set<Entry> getVisible() {
        try (var dirStream = Files.list(value)) {
            return dirStream
                    .map(Entry::fromPath)
                    .filter(Entry::isVisible)
                    .collect(toSet());

        } catch (IOException e) {
            return Set.of(ErrorEntry.withDefaultDepthLevel());
        }
    }

    @Override
    public Set<Entry> getVisibleEntriesRecursively(int maxDepth) {
        Set<Entry> result = new HashSet<>();
        collectEntries(this, result, maxDepth);

        return result
                .stream()
                .filter(Entry::isVisible)
                .collect(toSet());
    }

    private void collectEntries(Entry entry, Set<Entry> accumulator, int maxDepth) {
        if (entry.depthLevel() <= maxDepth) {
            accumulator.add(entry);
        }

        if (entry instanceof DirEntry dir && dir.depthLevel() <= maxDepth) {
            for (Entry child : dir.getRootLevelEntries()) {
                collectEntries(child, accumulator, maxDepth);
            }
        }
    }
}