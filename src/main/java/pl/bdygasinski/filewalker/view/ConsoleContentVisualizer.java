package pl.bdygasinski.filewalker.view;

import java.util.List;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

class ConsoleContentVisualizer implements ContentVisualizer {

    private final List<DisplayableEntry> entries;

    ConsoleContentVisualizer(List<DisplayableEntry> entries) {
        this.entries = requireNonNull(entries);
    }

    @Override
    public void listVisible() {
        printEntries(DisplayableEntry::entryName);
    }

    private void printEntries(Function<DisplayableEntry, String> customNameExtractor) {
        entries.stream()
                .map(customNameExtractor)
                .forEach(System.out::println);
    }
}