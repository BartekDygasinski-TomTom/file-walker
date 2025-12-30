package pl.bdygasinski.filewalker.model;

import java.nio.file.Path;

public record ErrorEntry(int depthLevel, Path path) implements Entry {

    public ErrorEntry {
        if (depthLevel < 0) {
            depthLevel = 0;
        }
    }

    @Override
    public String baseName() {
        return "";
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}