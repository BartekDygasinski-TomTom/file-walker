package pl.bdygasinski.filewalker;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.bdygasinski.filewalker.Entry.DirEntry;
import pl.bdygasinski.filewalker.Entry.FileEntry;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.bdygasinski.filewalker.ClassLoadingUtil.*;

@DisplayName("ConsoleContentProvider unit tests")
class ConsoleContentProviderTest {

    private final ContentProvider underTest = ContentProvider.getInstance();

    @TempDir
    private Path givenTempDir;

    @DisplayName("provideEntriesFrom() unit tests")
    @Nested
    class ProvideEntriesFromTest {

        @DisplayName("Should return file entry if path refers to regular file")
        @Test
        void should_detect_file() {
            // Given
            FileEntry givenFileEntry = new FileEntry("1.txt");
            URI givenUri = classpathResource("%s/2/%s".formatted(ROOT_DIR, givenFileEntry.value())).orElseThrow();
            Path givenPath = Path.of(givenUri);

            // When
            List<Entry> result = underTest.provideEntriesFrom(givenPath);

            // Then
            assertThat(result)
                    .hasSize(1)
                    .containsExactly(givenFileEntry);
        }

        @DisplayName("Should return entries from dir content instead of dir itself")
        @Test
        void should_detect_directory() {
            // Given
            DirEntry givenDir = new DirEntry("/A");
            URI givenUri = classpathResource(ROOT_DIR + givenDir.value()).orElseThrow();
            Path givenPath = Path.of(givenUri);

            // When
            List<Entry> result = underTest.provideEntriesFrom(givenPath);

            // Then
            assertThat(result)
                    .hasSizeGreaterThan(1)
                    .doesNotContain(givenDir);
        }

        @DisplayName("Should ignore entry if given path refers to hidden file")
        @Test
        void should_ignore_hidden_file() {
            // Given
            URI givenUri = classpathResource(HIDDEN_FILE).orElseThrow();
            Path givenPath = Path.of(givenUri);

            // When
            List<Entry> result = underTest.provideEntriesFrom(givenPath);

            // Then
            assertThat(result)
                    .isEmpty();
        }

        @DisplayName("Should ignore entry if given path refers to hidden directory")
        @Test
        void should_ignore_hidden_directory() {
            // Given
            URI givenUri = classpathResource(HIDDEN_DIR).orElseThrow();
            Path givenPath = Path.of(givenUri);

            // When
            List<Entry> result = underTest.provideEntriesFrom(givenPath);

            // Then
            assertThat(result)
                    .isEmpty();
        }

        @DisplayName("Should ignore hidden entries inside directory from given path")
        @Test
        void should_ignore_hidden_entries_in_directory() {
            // Given
            List<String> givenHiddenDirs = List.of(".A", ".B", ".C");
            List<String> givenHiddenFiles = List.of(".a.txt", ".b.txt", ".c.txt");
            List<String> givenVisibleFiles = List.of("a.txt", "b.txt");
            List<String> givenVisibleDirs = List.of("A", "B");

            var filesStream = Stream.concat(givenHiddenFiles.stream(), givenVisibleFiles.stream());
            var dirStream = Stream.concat(givenHiddenDirs.stream(), givenVisibleDirs.stream());

            createTmpFilesFromStreamAtDirectoryPath(filesStream, givenTempDir);
            createTmpDirsFromStreamAtDirectoryPath(dirStream, givenTempDir);

            // When
            List<Entry> result = underTest.provideEntriesFrom(givenTempDir);

            // Then
            assertThat(result)
                    .extracting(Entry::value)
                    .doesNotContainAnyElementsOf(givenHiddenDirs)
                    .doesNotContainAnyElementsOf(givenHiddenFiles)
                    .containsAll(givenVisibleDirs)
                    .containsAll(givenVisibleFiles);
        }

        @DisplayName("Should mark visible directories with [dir]")
        @Test
        void should_mark_visible_directories() {
            // Given
            List<String> givenVisibleDirs = List.of("A", "B");

            createTmpDirsFromStreamAtDirectoryPath(givenVisibleDirs.stream(), givenTempDir);

            // When
            List<Entry> result = underTest.provideEntriesFrom(givenTempDir);

            // Then
            assertThat(result)
                    .extracting(Entry::value)
                    .allMatch(name -> name.contains("[dir] "));
        }

        @DisplayName("Should not mark hidden directories with [dir]")
        @Test
        void should_not_mark_hidden_directories() {
            // Given
            List<String> givenVisibleDirs = List.of("A", "B");
            List<String> givenHiddenDirs = List.of(".A", ".B");
            Stream<String> dirsStream = Stream.concat(givenVisibleDirs.stream(), givenHiddenDirs.stream());

            createTmpDirsFromStreamAtDirectoryPath(dirsStream, givenTempDir);

            // When
            List<Entry> result = underTest.provideEntriesFrom(givenTempDir);

            // Then
            assertThat(result)
                    .extracting(Entry::value)
                    .containsExactlyInAnyOrderElementsOf(
                            givenVisibleDirs.stream()
                                    .map(name -> "[dir] " + name)
                                    .toList()
                    )
                    .doesNotContainAnyElementsOf(
                            givenHiddenDirs.stream()
                                    .map(name -> "[dir] " + name)
                                    .toList()
                    );
        }

        @DisplayName("Should not mark files with [dir]")
        @Test
        void should_not_mark_files() {
            // Given
            List<String> givenVisibleDirs = List.of("A", "B");
            List<String> givenFiles = List.of(".a", "b", "c.txt");

            createTmpDirsFromStreamAtDirectoryPath(givenVisibleDirs.stream(), givenTempDir);
            createTmpFilesFromStreamAtDirectoryPath(givenFiles.stream(), givenTempDir);

            // When
            List<Entry> result = underTest.provideEntriesFrom(givenTempDir);

            // Then
            assertThat(result)
                    .extracting(Entry::value)
                    .containsExactlyInAnyOrderElementsOf(
                            givenVisibleDirs.stream()
                                    .map(name -> "[dir] " + name)
                                    .toList()
                    )
                    .doesNotContainAnyElementsOf(
                            givenFiles.stream()
                                    .map(name -> "[dir] " + name)
                                    .toList()
                    );
        }
    }

    private static void createTmpFilesFromStreamAtDirectoryPath(Stream<String> stream, Path directoryPath) {
        if (!Files.isDirectory(directoryPath)) return;

        stream.map(Path::of).forEach(path -> {
            try {
                Files.createFile(directoryPath.resolve(path));

            } catch (IOException e) {
                throw new IllegalArgumentException();
            }
        });
    }

    private static void createTmpDirsFromStreamAtDirectoryPath(Stream<String> stream, Path directoryPath) {
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