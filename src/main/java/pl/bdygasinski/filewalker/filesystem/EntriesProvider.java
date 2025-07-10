package pl.bdygasinski.filewalker.filesystem;

import pl.bdygasinski.filewalker.model.Entry;

import java.nio.file.Path;
import java.util.List;

import static java.util.Objects.requireNonNull;

public interface EntriesProvider {

    List<Entry> getEntriesFromPath(Path path);

    static EntriesProvider withVisitor(EntryFileVisitor visitor) {
        return new FileSystemEntriesProvider(requireNonNull(visitor));
    }
}