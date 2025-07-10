package pl.bdygasinski.filewalker.filesystem;

import pl.bdygasinski.filewalker.model.Entry;

import java.nio.file.Path;
import java.util.List;

public interface EntriesProvider {

    List<Entry> getEntriesFromPath(Path path, int maxDepth);

    static EntriesProvider getDefault() {
        return new FileSystemEntriesProvider();
    }
}
