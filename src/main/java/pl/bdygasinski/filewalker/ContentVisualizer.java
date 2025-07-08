package pl.bdygasinski.filewalker;

import pl.bdygasinski.filewalker.model.Entry;

import java.util.Set;

import static java.util.Objects.requireNonNull;

public interface ContentVisualizer {

    static ContentVisualizer withEntries(Set<Entry> entries) {
        return new ConsoleContentVisualizer(entries);
    }

    void listVisible();
}

class ConsoleContentVisualizer implements ContentVisualizer {

    private final Set<Entry> entries;

    ConsoleContentVisualizer(Set<Entry> entries) {
        this.entries = requireNonNull(entries);
    }

    @Override
    public void listVisible() {
        entries.stream()
                .map(Entry::displayName)
                .forEach(System.out::println);
    }
}
