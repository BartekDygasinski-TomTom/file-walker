package pl.bdygasinski.filewalker.helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class TestTmpFileCreator {

    private TestTmpFileCreator() { }

    public static void createTmpFilesFromStreamAtDirectoryPath(Stream<String> stream, Path directoryPath) {
        if (!Files.isDirectory(directoryPath)) return;

        stream.map(Path::of).forEach(path -> {
            try {
                Files.createFile(directoryPath.resolve(path));

            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to create file at path: " + directoryPath.resolve(path), e);
            }
        });
    }

    public static void createTmpDirsFromStreamAtDirectoryPath(Stream<String> stream, Path directoryPath) {
        if (!Files.isDirectory(directoryPath)) return;

        stream.map(Path::of).forEach(path -> {
            try {
                Files.createDirectory(directoryPath.resolve(path));

            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to create directory at path: " + path, e);
            }
        });
    }
}
