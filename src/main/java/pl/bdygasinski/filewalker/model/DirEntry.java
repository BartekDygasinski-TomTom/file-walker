package pl.bdygasinski.filewalker.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.Objects.isNull;

public record DirEntry(String baseName, int depthLevel, boolean isVisible) implements Entry {

    public DirEntry {
        if (isNull(baseName)) {
            throw new IllegalArgumentException("Basename can't be null");
        }

        if (depthLevel < 0) {
            throw new IllegalArgumentException("Depth level must be positive");
        }
    }

    public static Entry fromPathAndDepthLevel(Path path, int depthLevel) {
        try {
            if (!Files.isDirectory(path)) {
                throw new IllegalArgumentException("Can't create DirEntry from file path");
            }

            String basename = path.getFileName().toString();
            return new DirEntry(basename, depthLevel, !Files.isHidden(path));

        } catch (IOException e) {
            return new ErrorEntry(depthLevel);
        }
    }

}
