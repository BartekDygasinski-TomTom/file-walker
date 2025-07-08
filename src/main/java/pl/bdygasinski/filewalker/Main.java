package pl.bdygasinski.filewalker;

import pl.bdygasinski.filewalker.model.Entry;

import java.nio.file.Path;
import java.util.Set;

import static java.util.Objects.isNull;

class Main {

    public static void main(String[] args) {
        validateInput(args);
        Path path = Path.of(args[0]);

        var provider = ContentProvider.getInstance();
        Set<Entry> entries = provider.provideEntriesFrom(path);

        var visualiser = ContentVisualizer.withEntries(entries);
        visualiser.listVisible();
    }

    private static void validateInput(String[] args) {
        if (isNull(args) || args.length == 0) {
            throw new IllegalArgumentException("At least one arg must be provided");
        }
    }
}
