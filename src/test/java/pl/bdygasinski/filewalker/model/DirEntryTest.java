package pl.bdygasinski.filewalker.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
import static org.mockito.ArgumentMatchers.anyInt;
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
            Exception result = catchException(() -> DirEntry.withDefaultDepthLevel(givenPath));

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
            Exception result = catchException(() -> DirEntry.withDefaultDepthLevel(givenPath));

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
            DirEntry underTest = DirEntry.withDefaultDepthLevel(givenPath);

            // When
            String result = underTest.displayName().name();

            // Then
            assertThat(result)
                    .isEqualTo("[dir] %s".formatted(givenBasename));
        }
    }

    @DisplayName("getRootLevelEntries() unit tests")
    @Nested
    class GetRootLevelEntriesTest {

        @DisplayName("Should return set with one ErrorEntry when path refers to unaccessible dir")
        @Test
        void shouldThrow() {
            // Given
            Path givenPath = Path.of(classpathResource(ROOT_DIR).orElseThrow());
            DirEntry underTest = DirEntry.withDefaultDepthLevel(givenPath);

            try (var entryMock = mockStatic(Entry.class)) {
                entryMock
                        .when(() -> Entry.fromPathWithDepthLevel(any(Path.class), anyInt()))
                        .thenReturn(ErrorEntry.withDefaultDepthLevel());
                // When
                Set<Entry> result = underTest.getRootLevelEntries();

                // Then
                assertThat(result)
                        .containsExactly(ErrorEntry.withDefaultDepthLevel());
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

            DirEntry underTest = DirEntry.withDefaultDepthLevel(givenTempDir);

            // When
            Set<Entry> result = underTest.getRootLevelEntries();

            // Then
            assertThat(result)
                    .extracting(entry -> entry.displayName().name())
                    .containsAll(Stream.concat(
                            dirItems.stream().map("[dir] %s"::formatted),
                            fileItems.stream()
                    ).toList());
        }

    }

    @DisplayName("getVisibleRootLevelEntries() unit tests")
    @Nested
    class GetVisibleRootLevelEntriesTest {

        @DisplayName("Should return set with one ErrorEntry when path refers to unaccessible dir")
        @Test
        void shouldThrow() {
            // Given
            Path givenPath = Path.of(classpathResource(ROOT_DIR).orElseThrow());
            DirEntry underTest = DirEntry.withDefaultDepthLevel(givenPath);

            try (var entryMock = mockStatic(Entry.class)) {
                entryMock
                        .when(() -> Entry.fromPath(any(Path.class)))
                        .thenReturn(ErrorEntry.withDefaultDepthLevel());
                // When
                Set<Entry> result = underTest.getVisibleRootLevelEntries();

                // Then
                assertThat(result)
                        .containsExactly(ErrorEntry.withDefaultDepthLevel());
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

            DirEntry underTest = DirEntry.withDefaultDepthLevel(givenTempDir);

            // When
            Set<Entry> result = underTest.getVisibleRootLevelEntries();

            // Then
            assertThat(result)
                    .allMatch(Entry::isVisible)
                    .extracting(entry -> entry.displayName().nameWithIndentation())
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
            DirEntry underTest = DirEntry.withDefaultDepthLevel(givenPath);

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
            DirEntry underTest = DirEntry.withDefaultDepthLevel(givenPath);

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
            DirEntry underTest = DirEntry.withDefaultDepthLevel(givenPath);

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
            DirEntry underTest = DirEntry.withDefaultDepthLevel(givenPath);

            // When
            Path result = underTest.value();

            // Then
            assertThat(result)
                    .isEqualTo(givenPath);
        }
    }

    @DisplayName("getVisibleEntriesRecursively(maxDepth) unit tests")
    @Nested
    class GetVisibleEntriesRecursivelyTest {

        @DisplayName("Should give only entries with less or equal depthLevel")
        @ParameterizedTest
        @ValueSource(ints = {0, 1, 2, 3})
        void shouldGiveOnlyEntriesWithLessOrEqualDepthLevel(int maxDepth) {
            // Given
            Path givenPath = Path.of(classpathResource(ROOT_DIR).orElseThrow());
            Entry underTest = Entry.fromPath(givenPath);

            // When
            Set<Entry> result = underTest.getVisibleEntriesRecursively(maxDepth);

            // Then
            assertThat(result)
                    .allMatch(entry -> entry.depthLevel() <= maxDepth);
        }

        @DisplayName("Should give entries exactly like in test data")
        @Test
        void shouldGiveEntriesExactlyLikeInTestData() {
            // Given
            Path givenPath = Path.of(classpathResource(ROOT_DIR).orElseThrow());
            Entry underTest = Entry.fromPath(givenPath);

            // When
            Set<Entry> result = underTest.getVisibleEntriesRecursively(2);

            // Then
            var expected = new Entry[]{
                    Entry.fromPathWithDepthLevel(Path.of(classpathResource(ROOT_DIR).orElseThrow()), 0),
                    Entry.fromPathWithDepthLevel(Path.of(classpathResource(ROOT_DIR + "/1").orElseThrow()), 1),
                    Entry.fromPathWithDepthLevel(Path.of(classpathResource(ROOT_DIR + "/2").orElseThrow()), 1),
                    Entry.fromPathWithDepthLevel(Path.of(classpathResource(ROOT_DIR + "/2/1.txt").orElseThrow()), 2),
                    Entry.fromPathWithDepthLevel(Path.of(classpathResource(ROOT_DIR + "/2/22.txt").orElseThrow()), 2),
                    Entry.fromPathWithDepthLevel(Path.of(classpathResource(ROOT_DIR + "/3").orElseThrow()), 1),
                    Entry.fromPathWithDepthLevel(Path.of(classpathResource(ROOT_DIR + "/3/README").orElseThrow()), 2),
                    Entry.fromPathWithDepthLevel(Path.of(classpathResource(ROOT_DIR + "/A").orElseThrow()), 1),
                    Entry.fromPathWithDepthLevel(Path.of(classpathResource(ROOT_DIR + "/A/a1").orElseThrow()), 2),
                    Entry.fromPathWithDepthLevel(Path.of(classpathResource(ROOT_DIR + "/A/a2").orElseThrow()), 2),
                    Entry.fromPathWithDepthLevel(Path.of(classpathResource(ROOT_DIR + "/A/a3").orElseThrow()), 2),
                    Entry.fromPathWithDepthLevel(Path.of(classpathResource(ROOT_DIR + "/B").orElseThrow()), 1),
                    Entry.fromPathWithDepthLevel(Path.of(classpathResource(ROOT_DIR + "/B/B.jpg").orElseThrow()), 2),
                    Entry.fromPathWithDepthLevel(Path.of(classpathResource(ROOT_DIR + "/C").orElseThrow()), 1),
            };
            assertThat(result)
                    .containsExactlyInAnyOrder(expected);
        }

        @DisplayName("Should skip hidden entries")
        @Test
        void shouldSkipHiddenEntries() {
            // Given
            Path givenPath = Path.of(classpathResource(ROOT2_DIR).orElseThrow());
            Entry underTest = Entry.fromPath(givenPath);

            // When
            Set<Entry> result = underTest.getVisibleEntriesRecursively(2);

            // Then
            var expected = new Entry[]{
                    Entry.fromPathWithDepthLevel(Path.of(classpathResource(ROOT2_DIR).orElseThrow()), 0),
                    Entry.fromPathWithDepthLevel(Path.of(classpathResource(ROOT2_DIR + "/1").orElseThrow()), 1),
                    Entry.fromPathWithDepthLevel(Path.of(classpathResource(ROOT2_DIR + "/2").orElseThrow()), 1),
                    Entry.fromPathWithDepthLevel(Path.of(classpathResource(ROOT2_DIR + "/3").orElseThrow()), 1),
                    Entry.fromPathWithDepthLevel(Path.of(classpathResource(ROOT2_DIR + "/A").orElseThrow()), 1),
                    Entry.fromPathWithDepthLevel(Path.of(classpathResource(ROOT2_DIR + "/B").orElseThrow()), 1),
                    Entry.fromPathWithDepthLevel(Path.of(classpathResource(ROOT2_DIR + "/C").orElseThrow()), 1),
            };
            assertThat(result)
                    .containsExactlyInAnyOrder(expected);
        }
    }
}