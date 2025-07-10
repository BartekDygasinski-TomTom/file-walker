package pl.bdygasinski.filewalker.filesystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pl.bdygasinski.filewalker.model.Entry;
import pl.bdygasinski.filewalker.model.ErrorEntry;
import pl.bdygasinski.filewalker.model.FileEntry;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;
import static pl.bdygasinski.filewalker.helper.TestClassLoadingUtil.*;

class FileSystemEntriesProviderTest {

    private EntryFileVisitor entryFileVisitor;
    private FileSystemEntriesProvider underTest;

    @BeforeEach
    void setUp() {
        entryFileVisitor = spy(new EntryFileVisitor(0, entry -> true));
        underTest = new FileSystemEntriesProvider(entryFileVisitor);
    }

    @DisplayName("construction()")
    @Nested
    class ConstructorTest {

        @DisplayName("Should throw if visitor is null")
        @Test
        void shouldThrowIfVisitorIsNull() {
            // When
            var result = catchException(() -> new FileSystemEntriesProvider(null));

            // Then
            assertThat(result)
                    .isNotNull()
                    .hasMessageContaining("null");
        }
    }

    @DisplayName("getEntriesFromPath()")
    @Nested
    class GetEntriesFromPathTest {

        @DisplayName("Should return list with one error entry if can't access path")
        @Test
        void shouldReturnListWithOneErrorEntry() {
            // Given
            var givenPath = pathWithoutValidation("notValid");
            var givenDepthLevel = 0;

            // When
            var result = underTest.getEntriesFromPath(givenPath);

            // Then
            assertThat(result)
                    .containsExactly(new ErrorEntry(givenDepthLevel, givenPath));
        }

        @DisplayName("Should return content from visitor if path is accessible")
        @Test
        void shouldReturnListWithGivenContent() {
            // Given
            var givenPath = pathFromClasspath(ROOT_DIR);
            var givenDepthLevel = 0;

            var expectedResult = List.of(
                    FileEntry.fromPathAndDepthLevel(givenPath.resolve("file1"), givenDepthLevel + 1),
                    FileEntry.fromPathAndDepthLevel(givenPath.resolve("file2"), givenDepthLevel + 1)
            );

            given(entryFileVisitor.getEntries())
                    .willReturn(expectedResult);

            // When
            var result = underTest.getEntriesFromPath(givenPath);

            // Then
            assertThat(result)
                    .containsAll(expectedResult);
        }

        @DisplayName("Should return entries with depth level till max depth value")
        @ParameterizedTest
        @ValueSource(ints = {0, 1, 2})
        void shouldReturnEntriesTillMaxDepthValue(int maxDepthValue) {
            // Given
            var givenPath = pathFromClasspath(ROOT_DIR);
            var givenVisitor = new EntryFileVisitor(maxDepthValue, entry -> true);
            var underTest = new FileSystemEntriesProvider(givenVisitor);

            // When
            List<Entry> result = underTest.getEntriesFromPath(givenPath);

            // Then
            assertThat(result)
                    .allMatch(entry -> entry.depthLevel() <= maxDepthValue);
        }
    }
}