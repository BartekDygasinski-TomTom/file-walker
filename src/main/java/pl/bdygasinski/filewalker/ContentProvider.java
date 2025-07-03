package pl.bdygasinski.filewalker;

import java.nio.file.Path;
import java.util.Set;

interface ContentProvider {

    static ContentProvider getInstance() {
        return new ConsoleContentProvider();
    }

    Set<Entry> provideEntriesFrom(Path path);
}
