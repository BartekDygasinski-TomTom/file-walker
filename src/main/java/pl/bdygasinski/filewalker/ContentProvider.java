package pl.bdygasinski.filewalker;

import pl.bdygasinski.filewalker.model.Entry;

import java.nio.file.Path;
import java.util.Set;

public interface ContentProvider {

    static ContentProvider getInstance() {
        return new ConsoleContentProvider();
    }

    Set<Entry> provideEntriesFrom(Path path);

    Set<Entry> provideEntriesRecursivelyFrom(Path path, int maxDepth);
}

class ConsoleContentProvider implements ContentProvider {

    @Override
    public Set<Entry> provideEntriesFrom(Path path) {
        Entry rootEntry = Entry.fromPath(path);
        return rootEntry.getVisibleRootLevelEntries();
    }

    @Override
    public Set<Entry> provideEntriesRecursivelyFrom(Path path, int maxDepth) {
        if (maxDepth < 0) {
            maxDepth = 0;
        }

        Entry entry = Entry.fromPath(path);
        return entry.getVisibleEntriesRecursively(maxDepth);
    }
}
