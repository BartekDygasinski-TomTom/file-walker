package pl.bdygasinski.filewalker.helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class TestTmpFileCreator {

    private TestTmpFileCreator() { }

    public static void createTmpFile(Stream<String> stream, Path directoryPath) {
        createTmpResource(stream, directoryPath, path -> {
            try {
                Files.createFile(directoryPath.resolve(path));

            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to create file at path: " + directoryPath.resolve(path), e);
            }
        });
    }

    public static void createTmpDir(Stream<String> stream, Path directoryPath) {
        createTmpResource(stream, directoryPath, path -> {
            try {
                Files.createDirectory(directoryPath.resolve(path));

            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to create directory at path: " + directoryPath.resolve(path), e);
            }
        });
    }

    public static void createTmpResource(Stream<String> stream, Path directoryPath, Consumer<Path> consumer) {
        if (!Files.isDirectory(directoryPath)) return;

        stream.map(Path::of).forEach(consumer);
    }
}