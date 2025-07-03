package pl.bdygasinski.filewalker;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Set;

class ConsoleContentProvider implements ContentProvider{

    @Override
    public Set<Entry> provideEntriesFrom(Path path) {
        try {
            Entry rootEntry = Entry.fromPathOrThrow(path);
            return rootEntry.getVisibleRootLevelOrThrow();

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
