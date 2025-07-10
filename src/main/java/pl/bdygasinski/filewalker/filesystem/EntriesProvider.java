package pl.bdygasinski.filewalker.filesystem;

import pl.bdygasinski.filewalker.model.Entry;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

public interface EntriesProvider {

    List<Entry> getEntriesFromPath(Path path, int maxDepth);

    static EntriesProvider withFilter(Predicate<Entry> filter) {
        return new FileSystemEntriesProvider(filter);
    }
}
