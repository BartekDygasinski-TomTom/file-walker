package pl.bdygasinski.filewalker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

class TmpFileCreator {

    private TmpFileCreator() {
        throw new UnsupportedOperationException("Can't create util class");
    }

    static void createTmpFilesFromStreamAtDirectoryPath(Stream<String> stream, Path directoryPath) {
        if (!Files.isDirectory(directoryPath)) return;

        stream.map(Path::of).forEach(path -> {
            try {
                Files.createFile(directoryPath.resolve(path));

            } catch (IOException e) {
                throw new IllegalArgumentException();
            }
        });
    }

    static void createTmpDirsFromStreamAtDirectoryPath(Stream<String> stream, Path directoryPath) {
        if (!Files.isDirectory(directoryPath)) return;

        stream.map(Path::of).forEach(path -> {
            try {
                Files.createDirectory(directoryPath.resolve(path));

            } catch (IOException e) {
                throw new IllegalArgumentException();
            }
        });
    }
}
