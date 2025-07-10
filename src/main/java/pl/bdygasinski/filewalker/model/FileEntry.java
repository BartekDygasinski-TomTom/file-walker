package pl.bdygasinski.filewalker.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

import static java.util.Objects.requireNonNull;

public final class FileEntry implements Entry {

    private final Path path;
    private final int depthLevel;
    private final boolean isVisible;

    private FileEntry(Path path, int depthLevel, boolean isVisible) {
        if (Files.isDirectory(path)) {
            throw new IllegalArgumentException("Can't create FileEntry from directory path");
        }

        if (depthLevel < 0) {
            throw new IllegalArgumentException("Depth level must be positive but got %s".formatted(depthLevel));
        }

        this.path = requireNonNull(path);
        this.depthLevel = depthLevel;
        this.isVisible = isVisible;
    }

    public static Entry fromPathAndDepthLevel(Path path, int depthLevel) {
        try {
            return new FileEntry(path, depthLevel, !Files.isHidden(path));

        } catch (IOException e) {
            return new ErrorEntry(depthLevel, path);
        }
    }

    @Override
    public String baseName() {
        return path.getFileName().toString();
    }

    @Override
    public int depthLevel() {
        return depthLevel;
    }

    @Override
    public boolean isVisible() {
        return isVisible;
    }

    @Override
    public Optional<Long> sizeInBytes() {
        try {
            return Optional.of(Files.size(path));

        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> fileExtension() {
        String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return Optional.of(fileName.substring(dotIndex + 1));
        }

        return Optional.empty();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FileEntry fileEntry)) return false;
        return depthLevel == fileEntry.depthLevel && isVisible == fileEntry.isVisible && Objects.equals(path, fileEntry.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, depthLevel, isVisible);
    }

    @Override
    public String toString() {
        return new StringJoiner(",", "FileEntry{", "}")
                .add("path=").add(path.toString())
                .add("depthLevel=").add(String.valueOf(depthLevel))
                .add("isVisible=").add(String.valueOf(isVisible))
                .toString();
    }
}
