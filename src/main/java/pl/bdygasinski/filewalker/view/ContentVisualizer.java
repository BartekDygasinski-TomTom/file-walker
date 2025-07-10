package pl.bdygasinski.filewalker.view;

import java.util.List;

public interface ContentVisualizer {

    void listVisible();

    static ContentVisualizer getDefault(List<DisplayableEntry> entries) {
        return new ConsoleContentVisualizer(entries);
    }
}

