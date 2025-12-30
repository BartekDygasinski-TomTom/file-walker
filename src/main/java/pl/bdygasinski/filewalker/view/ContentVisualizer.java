package pl.bdygasinski.filewalker.view;

import java.util.List;

import static java.util.Objects.requireNonNull;

public interface ContentVisualizer {

    void listVisible();

    static ContentVisualizer forEntries(List<DisplayableEntry> entries) {
        return new ConsoleContentVisualizer(requireNonNull(entries));
    }
}