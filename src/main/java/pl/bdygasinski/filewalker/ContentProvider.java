package pl.bdygasinski.filewalker;

import java.nio.file.Path;
import java.util.List;

interface ContentProvider {

    static ContentProvider getInstance() {
        return new ConsoleContentProvider();
    }

    List<Entry> provideEntries(Path path);
}
