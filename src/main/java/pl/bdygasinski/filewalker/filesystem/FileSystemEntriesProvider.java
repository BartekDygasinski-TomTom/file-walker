package pl.bdygasinski.filewalker.filesystem;

import pl.bdygasinski.filewalker.model.Entry;
import pl.bdygasinski.filewalker.model.ErrorEntry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class FileSystemEntriesProvider implements EntriesProvider {

    @Override
    public List<Entry> getEntriesFromPath(Path path, int maxDepth) {
        EntryFileVisitor visitor = new EntryFileVisitor(maxDepth);

        try {
            Files.walkFileTree(path, visitor);

        } catch (IOException e) {
            return List.of(new ErrorEntry(maxDepth));
        }

        return visitor.getEntries();
    }
}
