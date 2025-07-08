package pl.bdygasinski.filewalker;

import pl.bdygasinski.filewalker.model.Entry;

import java.nio.file.Path;
import java.util.Set;

public interface ContentProvider {

    static ContentProvider getInstance() {
        return new ConsoleContentProvider();
    }

    Set<Entry> provideEntriesFrom(Path path);
}

class ConsoleContentProvider implements ContentProvider {

    @Override
    public Set<Entry> provideEntriesFrom(Path path) {
        Entry rootEntry = Entry.fromPath(path);
        return rootEntry.getVisibleRootLevelEntries();
    }
}
