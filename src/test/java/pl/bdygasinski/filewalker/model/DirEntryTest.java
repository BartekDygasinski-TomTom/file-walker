package pl.bdygasinski.filewalker.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pl.bdygasinski.filewalker.exception.EntryNotAccessibleException;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static pl.bdygasinski.filewalker.helper.TestClassLoadingUtil.*;
import static pl.bdygasinski.filewalker.helper.TestTmpFileCreator.createTmpDirsFromStreamAtDirectoryPath;
import static pl.bdygasinski.filewalker.helper.TestTmpFileCreator.createTmpFilesFromStreamAtDirectoryPath;

class DirEntryTest {

    @TempDir
    private Path givenTempDir;

    @DisplayName("Constructor unit tests")
    @Nested
    class ConstructorTest {

        @DisplayName("Should throw IllegalArgumentException when path is null")
        @Test
        void shouldThrowIfPathIsNull() {
            // Given
            Path givenPath = null;

            // When
            Exception result = catchException(() -> new DirEntry(givenPath));

            // Then
            assertThat(result)
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("Should throw IllegalArgumentException when path doesn't refer directory")
        @Test
        void shouldThrowIfPathDoesntReferDirectory() {
            // Given
            URI givenUri = classpathResource(ROOT_DIR + "/2/1.txt").orElseThrow();
            Path givenPath = Path.of(givenUri);

            // When
            Exception result = catchException(() -> new DirEntry(givenPath));

            // Then
            assertThat(result)
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("displayName() unit tests")
    @Nested
    class DisplayNameTest {

        @DisplayName("Should display base name as display name with marked [dir]")
        @Test
        void shouldDisplayBasename() {
            // Given
            Path givenPath = Path.of(classpathResource(ROOT_DIR).orElseThrow());
            String givenBasename = ROOT_DIR.replace("/", "");
            DirEntry underTest = new DirEntry(givenPath);

            // When
            String result = underTest.displayName();

            // Then
            assertThat(result)
                    .isEqualTo("[dir] %s".formatted(givenBasename));
        }
    }

    @DisplayName("getAllRootLevelOrThrow() unit tests")
    @Nested
    class GetAllRootLevelOrThrowTest {

        @DisplayName("Should throw if entries can't be created")
        @Test
        void shouldThrow() {
            // Given
            Path givenPath = Path.of(classpathResource(ROOT_DIR).orElseThrow());
            DirEntry underTest = new DirEntry(givenPath);

            try (var entryMock = mockStatic(Entry.class)) {
                entryMock
                        .when(() -> Entry.fromPathOrThrow(any(Path.class)))
                        .thenThrow(IllegalArgumentException.class);
                // When
                Exception result = catchException(underTest::getAllRootLevelOrThrow);

                // Then
                assertThat(result)
                        .isExactlyInstanceOf(IOException.class);
            }
        }

        @DisplayName("Should map directory content to set of entries including all items")
        @Test
        void shouldMapAndIncludeAllItems() throws IOException {
            // Given
            List<String> dirItems = List.of("A", ".B");
            List<String> fileItems = List.of("a", ".b");

            createTmpDirsFromStreamAtDirectoryPath(dirItems.stream(), givenTempDir);
            createTmpFilesFromStreamAtDirectoryPath(fileItems.stream(), givenTempDir);

            DirEntry underTest = new DirEntry(givenTempDir);

            // When
            Set<Entry> result = underTest.getAllRootLevelOrThrow();

            // Then
            assertThat(result)
                    .extracting(Entry::displayName)
                    .containsAll(Stream.concat(
                            dirItems.stream().map("[dir] %s"::formatted),
                            fileItems.stream()
                    ).toList());
        }

    }

    @DisplayName("getVisibleRootLevelOrThrow() unit tests")
    @Nested
    class GetVisibleRootLevelOrThrowTest {

        @DisplayName("Should throw if of of entries can't be created")
        @Test
        void shouldThrow() {
            // Given
            Path givenPath = Path.of(classpathResource(ROOT_DIR).orElseThrow());
            DirEntry underTest = new DirEntry(givenPath);

            try (var entryMock = mockStatic(Entry.class)) {
                entryMock
                        .when(() -> Entry.fromPathOrThrow(any(Path.class)))
                        .thenThrow(IllegalArgumentException.class);
                // When
                Exception result = catchException(underTest::getVisibleRootLevelOrThrow);

                // Then
                assertThat(result)
                        .isExactlyInstanceOf(IOException.class);
            }
        }

        @DisplayName("Should map directory content to set of entries without hidden one")
        @Test
        void shouldMapAndIncludeAllItems() throws IOException {
            // Given
            List<String> dirItems = List.of("A", ".B");
            List<String> fileItems = List.of("a", ".b");

            createTmpDirsFromStreamAtDirectoryPath(dirItems.stream(), givenTempDir);
            createTmpFilesFromStreamAtDirectoryPath(fileItems.stream(), givenTempDir);

            DirEntry underTest = new DirEntry(givenTempDir);

            // When
            Set<Entry> result = underTest.getVisibleRootLevelOrThrow();

            // Then
            assertThat(result)
                    .allMatch(Entry::isVisible)
                    .extracting(Entry::displayName)
                    .containsAll(Stream.concat(
                            dirItems.stream()
                                    .filter(not(item -> item.startsWith(".")))
                                    .map("[dir] %s"::formatted),
                            fileItems.stream()
                                    .filter(not(item -> item.startsWith(".")))
                    ).toList());
        }

    }

    @DisplayName("isVisible() unit tests")
    @Nested
    class IsVisibleTest {

        @DisplayName("Should throw UncheckedIOException when can't check visibility")
        @Test
        void shouldThrow() {
            // Given
            Path givenPath = Path.of(classpathResource(ROOT_DIR).orElseThrow());
            DirEntry underTest = new DirEntry(givenPath);

            Exception result;

            try (var filesMock = mockStatic(Files.class)) {
                filesMock
                        .when(() -> Files.isHidden(givenPath))
                        .thenThrow(new IOException());

                // When
                result = catchException(underTest::isVisible);
            }

            // Then
            assertThat(result)
                    .isExactlyInstanceOf(EntryNotAccessibleException.class)
                    .extracting(Throwable::getCause)
                    .isExactlyInstanceOf(IOException.class);
        }

        @DisplayName("Should return true if file is not hidden")
        @Test
        void shouldReturnTrue() {
            // Given
            Path givenPath = Path.of(classpathResource(ROOT_DIR).orElseThrow());
            DirEntry underTest = new DirEntry(givenPath);

            // When
            boolean result = underTest.isVisible();

            // Then
            assertThat(result)
                    .isTrue();
        }

        @DisplayName("Should return false if file is hidden")
        @Test
        void shouldReturnFalse() {
            // Given
            Path givenPath = Path.of(classpathResource(HIDDEN_DIR).orElseThrow());
            DirEntry underTest = new DirEntry(givenPath);

            // When
            boolean result = underTest.isVisible();

            // Then
            assertThat(result)
                    .isFalse();
        }
    }

    @DisplayName("value() unit tests")
    @Nested
    class ValueTest {

        @DisplayName("Should return path")
        @Test
        void shouldReturnPath() {
            // Given
            URI givenUri = classpathResource(ROOT_DIR).orElseThrow();
            Path givenPath = Path.of(givenUri);
            DirEntry underTest = new DirEntry(givenPath);

            // When
            Path result = underTest.value();

            // Then
            assertThat(result)
                    .isEqualTo(givenPath);
        }
    }
}