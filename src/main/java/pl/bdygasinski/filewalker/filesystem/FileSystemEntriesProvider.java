package pl.bdygasinski.filewalker.filesystem;

import pl.bdygasinski.filewalker.model.Entry;
import pl.bdygasinski.filewalker.model.ErrorEntry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.util.Objects.requireNonNull;

class FileSystemEntriesProvider implements EntriesProvider {

    private final EntryFileVisitor entryFileVisitor;

    FileSystemEntriesProvider(EntryFileVisitor entryFileVisitor) {
        this.entryFileVisitor = requireNonNull(entryFileVisitor, "Visitor is required but got %s".formatted(entryFileVisitor));
    }

    @Override
    public List<Entry> getEntriesFromPath(Path path) {
        try {
            Files.walkFileTree(path, entryFileVisitor);
            return entryFileVisitor.getEntries();

        } catch (IOException e) {
            return List.of(new ErrorEntry(entryFileVisitor.getMaxDepth(), path));
        }
    }
}