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
        if (allEntriesSameDepth()) {
            printEntries(DisplayableEntry::entryName);
        } else {
            printEntries(DisplayableEntry::entryNameWithIndentation);
        }
    }

    private boolean allEntriesSameDepth() {
        if (entries.isEmpty()) return true;

        int expectedDepth = entries.getFirst().entry().depthLevel();
        return entries.stream()
                .allMatch(e -> e.entry().depthLevel() == expectedDepth);
    }

    private void printEntries(Function<DisplayableEntry, String> customNameExtractor) {
        entries.stream()
                .map(customNameExtractor)
                .forEach(System.out::println);
    }
}