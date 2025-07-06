package pl.bdygasinski.filewalker;

import java.nio.file.Path;
import java.util.Set;

import static java.util.Objects.requireNonNull;

class ConsoleContentVisualizer implements ContentVisualizer{

    private final ContentProvider contentProvider;

    ConsoleContentVisualizer(ContentProvider contentProvider) {
        this.contentProvider = requireNonNull(contentProvider);
    }

    @Override
    public void listVisible(Path path) {
        Set<Entry> entries = contentProvider.provideEntriesFrom(requireNonNull(path));
        entries.stream()
                .map(Entry::displayName)
                .forEach(System.out::println);
    }
}
