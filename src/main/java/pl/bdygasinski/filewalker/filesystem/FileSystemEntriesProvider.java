package pl.bdygasinski.filewalker.filesystem;

import pl.bdygasinski.filewalker.model.Entry;
import pl.bdygasinski.filewalker.model.ErrorEntry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

class FileSystemEntriesProvider implements EntriesProvider {

    private final Predicate<Entry> filter;

    FileSystemEntriesProvider(Predicate<Entry> filter) {
        this.filter = requireNonNull(filter);
    }

    @Override
    public List<Entry> getEntriesFromPath(Path path, int maxDepth) {
        EntryFileVisitor visitor = new EntryFileVisitor(maxDepth, filter);

        try {
            Files.walkFileTree(path, visitor);

        } catch (IOException e) {
            return List.of(new ErrorEntry(maxDepth, path));
        }

        return visitor.getEntries();
    }
}
