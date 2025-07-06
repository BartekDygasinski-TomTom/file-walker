package pl.bdygasinski.filewalker;

import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

public interface ContentVisualizer {

    void listVisible(Path path);



    static ContentVisualizer getInstance(ContentProvider contentProvider) {
        return new ConsoleContentVisualizer(requireNonNull(contentProvider));
    }
}
