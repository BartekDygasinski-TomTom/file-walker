package pl.bdygasinski.filewalker;

import java.nio.file.Path;
import java.util.List;

class ConsoleContentProvider implements ContentProvider{

    @Override
    public List<Entry> provideEntriesFrom(Path path) {
        throw new UnsupportedOperationException();
    }
}
