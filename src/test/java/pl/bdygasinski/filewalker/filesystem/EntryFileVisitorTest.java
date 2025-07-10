package pl.bdygasinski.filewalker.filesystem;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pl.bdygasinski.filewalker.model.DirEntry;
import pl.bdygasinski.filewalker.model.Entry;
import pl.bdygasinski.filewalker.model.ErrorEntry;
import pl.bdygasinski.filewalker.model.FileEntry;

import java.nio.file.FileVisitResult;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static pl.bdygasinski.filewalker.helper.TestClassLoadingUtil.*;

class EntryFileVisitorTest {

    @DisplayName("constructor()")
    @Nested
    class ConstructorTest {

        @DisplayName("Should throw if maxDepth is negative")
        @ParameterizedTest
        @ValueSource(ints = {Integer.MIN_VALUE, -1})
        void shouldThrowIfMaxDepthIsNegative(int maxDepth) {
            // When
            var result = catchException(() -> new EntryFileVisitor(maxDepth, entry -> true));

            // Then
            assertThat(result)
                    .isNotNull()
                    .hasMessageContaining(String.valueOf(maxDepth));
        }

        @DisplayName("Should throw if filter is null")
        @Test
        void shouldThrowWithoutFilter() {
            // When
            var result = catchException(() -> new EntryFileVisitor(0, null));

            // Then
            assertThat(result)
                    .isNotNull()
                    .hasMessageContaining("null");
        }

        @DisplayName("Should set current depth at 0 when depth is > 0")
        @ParameterizedTest
        @ValueSource(ints = {1, Integer.MAX_VALUE})
        void shouldSetCurDepth0(int maxDepth) {
            // Given
            var underTest = new EntryFileVisitor(maxDepth, entry -> true);

            // When
            var result = underTest.getCurrDepth();

            // Then
            assertThat(result)
                    .isEqualTo(0);
        }
    }

    @DisplayName("getEntries()")
    @Nested
    class GetEntriesTest {

        @DisplayName("Should give unmodifiable copy of input list")
        @Test
        void shouldCopyInput() {
            // Given
            var givenContent = List.of(
                    FileEntry.fromPathAndDepthLevel(pathFromClasspath(FILE_WITHOUT_EXTENSION), 0),
                    FileEntry.fromPathAndDepthLevel(pathFromClasspath(TEXT_FILE), 0)
            );
            var underTest = new EntryFileVisitor(0, entry -> true, givenContent);

            // When
            var result = underTest.getEntries();

            // Then
            assertThat(result)
                    .containsExactlyElementsOf(givenContent)
                    .isNotSameAs(givenContent);

            Entry entry = FileEntry.fromPathAndDepthLevel(pathFromClasspath(HTML_FILE), 0);
            assertThatThrownBy(() -> result.add(entry))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

    }

    @DisplayName("preVisitDirectory()")
    @Nested
    class PreVisitDirectoryTest {
        
        @DisplayName("Should skip if curr depth is bigger than max depth")
        @Test
        void shouldSkip() {
            // Given
            var givenPath = pathFromClasspath(ROOT_DIR);
            var underTest = new EntryFileVisitor(0, entry -> true);
            var currentDepth = underTest.incrementCurrDepth();

            // When
            var result = underTest.preVisitDirectory(givenPath, null);

            // Then
            assertThat(currentDepth)
                    .isGreaterThan(underTest.getMaxDepth());

            assertThat(result)
                    .isEqualTo(FileVisitResult.SKIP_SUBTREE);
        }

        @DisplayName("Should skip adding dir entry to collection if curr and max depth is 0")
        @Test
        void shouldSkipAddingDirEntryToCollectionIfCurrAndMaxDepth0() {
            // Given
            var maxDepth = 0;
            List<Entry> data = List.of();
            var givenPath = pathFromClasspath(ROOT_DIR);
            var underTest = new EntryFileVisitor(maxDepth, entry -> true, data);
            var currDepth = underTest.getCurrDepth();

            // When
            var result = underTest.preVisitDirectory(givenPath, null);

            // Then
            assertThat(currDepth)
                    .isEqualTo(maxDepth)
                    .isEqualTo(0);

            assertThat(result)
                    .isEqualTo(FileVisitResult.CONTINUE);

            assertThat(underTest.getEntries())
                    .isEmpty();
        }

        @DisplayName("Should add entry to collection if curr depth !> max depth and max depth isn't 0")
        @Test
        void shouldAddEntry() {
            // Given
            var givenPath = pathFromClasspath(ROOT_DIR);
            var givenData = List.<Entry>of();
            var underTest = new EntryFileVisitor(1, entry -> true, givenData);

            // When
            var result = underTest.preVisitDirectory(givenPath, null);

            // Then
            assertThat(result)
                    .isEqualTo(FileVisitResult.CONTINUE);

            assertThat(underTest.getEntries())
                    .isNotEqualTo(givenData)
                    .hasSize(1)
                    .first()
                    .isInstanceOf(DirEntry.class);
        }
    }

    @DisplayName("postVisitDirectory()")
    @Nested
    class PostVisitDirectoryTest {

        @DisplayName("Should decrease currentDepth and continue")
        @Test
        void shouldDecreaseCurrentDepthAndContinue() {
            // Given
            var underTest = new EntryFileVisitor(0, entry -> true);
            var givenPath = pathFromClasspath(ROOT_DIR);
            var startCurrDepth = underTest.getCurrDepth();

            // When
            var result = underTest.postVisitDirectory(givenPath, null);

            // Then
            assertThat(result)
                    .isEqualTo(FileVisitResult.CONTINUE);

            assertThat(underTest.getCurrDepth())
                    .isEqualTo(startCurrDepth - 1);
        }
    }

    @DisplayName("visitFile()")
    @Nested
    class VisitFileTest {

        @DisplayName("Should update data if current depth <= max depth and filter gives true")
        @Test
        void shouldUpdateData() {
            // Given
            var givenPath = pathFromClasspath(TEXT_FILE);
            var givenData = List.<Entry>of();
            var givenMaxDepth = 0;
            var underTest = new EntryFileVisitor(givenMaxDepth, entry -> true, givenData);
            var currDepth = underTest.getCurrDepth();

            // When
            var result = underTest.visitFile(givenPath, null);

            // Then
            assertThat(result)
                    .isEqualTo(FileVisitResult.CONTINUE);

            assertThat(currDepth)
                    .isLessThanOrEqualTo(givenMaxDepth);

            assertThat(underTest.getEntries())
                    .isNotEqualTo(givenData)
                    .hasSize(1)
                    .first()
                    .isInstanceOf(FileEntry.class);
        }

        @DisplayName("Should not update data if current depth <= max depth and filter gives false")
        @Test
        void shouldNotUpdateWhenFilterReturnsFalse() {
            // Given
            var givenPath = pathFromClasspath(TEXT_FILE);
            var givenData = List.<Entry>of();
            var givenMaxDepth = 0;
            var underTest = new EntryFileVisitor(givenMaxDepth, entry -> false, givenData);
            var currDepth = underTest.getCurrDepth();

            // When
            var result = underTest.visitFile(givenPath, null);

            // Then
            assertThat(result)
                    .isEqualTo(FileVisitResult.CONTINUE);

            assertThat(currDepth)
                    .isLessThanOrEqualTo(givenMaxDepth);

            assertThat(underTest.getEntries())
                    .isEmpty();
        }

        @DisplayName("Should not update data if current depth > max depth")
        @Test
        void shouldNotUpdateData() {
            // Given
            var givenPath = pathFromClasspath(TEXT_FILE);
            var givenData = List.<Entry>of();
            var givenMaxDepth = 0;
            var underTest = new EntryFileVisitor(givenMaxDepth, entry -> true, givenData);
            var currentDepth = underTest.incrementCurrDepth();

            // When
            var result = underTest.visitFile(givenPath, null);

            // Then
            assertThat(result)
                    .isEqualTo(FileVisitResult.CONTINUE);

            assertThat(currentDepth)
                    .isGreaterThan(givenMaxDepth);

            assertThat(underTest.getEntries())
                    .isEmpty();
        }
    }

    @DisplayName("visitFileFailed()")
    @Nested
    class VisitFileFailedTest {

        @DisplayName("Should add error entry and continue")
        @Test
        void shouldAddErrorEntryAndContinue() {
            // Given
            var givenList = List.<Entry>of();
            var givenPath = pathWithoutValidation("wrong");
            var underTest = new EntryFileVisitor(0, entry -> true, givenList);

            // When
            var result = underTest.visitFileFailed(givenPath, null);

            // Then
            assertThat(result)
                    .isEqualTo(FileVisitResult.CONTINUE);

            List<Entry> updatedData = underTest.getEntries();
            assertThat(updatedData)
                    .isNotEqualTo(givenList)
                    .hasSize(1)
                    .first()
                    .isInstanceOf(ErrorEntry.class);
        }
    }
}