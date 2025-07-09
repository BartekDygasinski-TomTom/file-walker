package pl.bdygasinski.filewalker;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.bdygasinski.filewalker.model.Entry;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.bdygasinski.filewalker.helper.TestClassLoadingUtil.*;
import static pl.bdygasinski.filewalker.helper.TestTmpFileCreator.createTmpDirsFromStreamAtDirectoryPath;
import static pl.bdygasinski.filewalker.helper.TestTmpFileCreator.createTmpFilesFromStreamAtDirectoryPath;

class ConsoleContentProviderTest {

    private final ContentProvider underTest = ContentProvider.getInstance();

    @TempDir
    private Path givenTempDir;

    @DisplayName("provideEntriesFrom() unit tests")
    @Nested
    class ProvideEntriesFromTest {

        @DisplayName("Should return file entry if path refers to regular file")
        @Test
        void shouldDetectFile() {
            // Given
            URI givenUri = classpathResource(ROOT_DIR + "/2/1.txt").orElseThrow();
            Path givenPath = Path.of(givenUri);
            Entry givenEntry = Entry.fromPath(givenPath);

            // When
            Set<Entry> result = underTest.provideEntriesFrom(givenPath);

            // Then
            assertThat(result)
                    .hasSize(1)
                    .containsExactly(givenEntry);
        }

        @DisplayName("Should return entries from dir content instead of dir itself")
        @Test
        void shouldDetectDirectory() {
            // Given
            URI givenUri = classpathResource(ROOT_DIR + "/A").orElseThrow();
            Path givenPath = Path.of(givenUri);
            Entry givenEntry = Entry.fromPath(givenPath);

            // When
            Set<Entry> result = underTest.provideEntriesFrom(givenPath);

            // Then
            assertThat(result)
                    .hasSizeGreaterThan(1)
                    .doesNotContain(givenEntry);
        }

        @DisplayName("Should ignore entry if given path refers to hidden file")
        @Test
        void shouldIgnoreHiddenFile() {
            // Given
            URI givenUri = classpathResource(HIDDEN_FILE).orElseThrow();
            Path givenPath = Path.of(givenUri);

            // When
            Set<Entry> result = underTest.provideEntriesFrom(givenPath);

            // Then
            assertThat(result)
                    .isEmpty();
        }

        @DisplayName("Should ignore entry if given path refers to hidden directory")
        @Test
        void shouldIgnoreHiddenDirectory() {
            // Given
            URI givenUri = classpathResource(HIDDEN_DIR).orElseThrow();
            Path givenPath = Path.of(givenUri);

            // When
            Set<Entry> result = underTest.provideEntriesFrom(givenPath);

            // Then
            assertThat(result)
                    .isEmpty();
        }

        @DisplayName("Should ignore hidden entries inside directory from given path")
        @Test
        void shouldIgnoreHiddenEntriesInDirectory() {
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
            Set<Entry> result = underTest.provideEntriesFrom(givenTempDir);

            // Then
            assertThat(result)
                    .allMatch(Entry::isVisible);
        }

        @DisplayName("Should mark visible directories with [dir]")
        @Test
        void shouldMarkVisibleDirectories() {
            // Given
            List<String> givenVisibleDirs = List.of("A", "B");

            createTmpDirsFromStreamAtDirectoryPath(givenVisibleDirs.stream(), givenTempDir);

            // When
            Set<Entry> result = underTest.provideEntriesFrom(givenTempDir);

            // Then
            assertThat(result)
                    .isNotEmpty()
                    .extracting(entry -> entry.displayName().nameWithIndentation())
                    .allMatch(name -> name.contains("[dir] "));
        }

        @DisplayName("Should not mark hidden directories with [dir]")
        @Test
        void shouldNotMarkHiddenDirectories() {
            // Given
            List<String> givenVisibleDirs = List.of("A", "B");
            List<String> givenHiddenDirs = List.of(".A", ".B");
            Stream<String> dirsStream = Stream.concat(givenVisibleDirs.stream(), givenHiddenDirs.stream());

            createTmpDirsFromStreamAtDirectoryPath(dirsStream, givenTempDir);

            // When
            Set<Entry> result = underTest.provideEntriesFrom(givenTempDir);

            // Then
            assertThat(result)
                    .extracting(entry -> entry.displayName().nameWithIndentation())
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
    }
}