package pl.bdygasinski.filewalker.model;

import java.nio.file.Path;

public record ErrorEntry(int depthLevel, Path path) implements Entry {

    @Override
    public String baseName() {
        return "Unaccessible entry: %s".formatted(path);
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
