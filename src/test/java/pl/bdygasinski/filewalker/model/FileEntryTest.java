package pl.bdygasinski.filewalker.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pl.bdygasinski.filewalker.exception.EntryNotAccessibleException;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.Mockito.mockStatic;
import static pl.bdygasinski.filewalker.helper.TestClassLoadingUtil.*;

class FileEntryTest {

    @DisplayName("Constructor unit tests")
    @Nested
    class ConstructorTest {

        @DisplayName("Should throw IllegalArgumentException when path is null")
        @Test
        void shouldThrowIfPathIsNull() {
            // Given
            Path givenPath = null;

            // When
            Exception result = catchException(() -> new FileEntry(givenPath));

            // Then
            assertThat(result)
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("Should throw IllegalArgumentException when path refer directory")
        @Test
        void shouldThrowIfPathRefersDirectory() {
            // Given
            URI givenUri = classpathResource(ROOT_DIR).orElseThrow();
            Path givenPath = Path.of(givenUri);

            // When
            Exception result = catchException(() -> new FileEntry(givenPath));

            // Then
            assertThat(result)
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("displayName() unit tests")
    @Nested
    class DisplayNameTest {

        @DisplayName("Should display base name as display name")
        @Test
        void shouldDisplayBasename() {
            // Given
            String givenFileBasename = "1.txt";
            URI givenUri = classpathResource(ROOT_DIR + "/2/" + givenFileBasename).orElseThrow();
            Path givenPath = Path.of(givenUri);
            FileEntry underTest = new FileEntry(givenPath);

            // When
            String result = underTest.displayName();

            // Then
            assertThat(result)
                    .isEqualTo(givenFileBasename);
        }
    }

    @DisplayName("getRootLevelEntries() unit tests")
    @Nested
    class GetRootLevelEntriesTest {

        @DisplayName("Should return set with single entry when path refers to visible file")
        @Test
        void shouldReturnSetWithSingleEntry() {
            // Given
            String givenFileBasename = "1.txt";
            URI givenUri = classpathResource(ROOT_DIR + "/2/" + givenFileBasename).orElseThrow();
            Path givenPath = Path.of(givenUri);
            FileEntry underTest = new FileEntry(givenPath);

            // When
            Set<Entry> result = underTest.getRootLevelEntries();

            // Then
            assertThat(result)
                    .containsExactly(underTest);
        }
    }

    @DisplayName("getVisibleRootLevelEntries() unit tests")
    @Nested
    class GetVisibleRootLevelEntriesTest {

        @DisplayName("Should return empty set if entry is not visible")
        @Test
        void shouldReturnEmptySet() {
            // Given
            URI givenUri = classpathResource(HIDDEN_FILE).orElseThrow();
            Path givenPath = Path.of(givenUri);
            FileEntry underTest = new FileEntry(givenPath);

            // When
            Set<Entry> result = underTest.getVisibleRootLevelEntries();

            // Then
            assertThat(result)
                    .isEmpty();
        }

        @DisplayName("Should return set with single element when file entry is visible")
        @Test
        void shouldReturnSetWithOneItem() {
            // Given
            String givenFileBasename = "1.txt";
            URI givenUri = classpathResource(ROOT_DIR + "/2/" + givenFileBasename).orElseThrow();
            Path givenPath = Path.of(givenUri);
            FileEntry underTest = new FileEntry(givenPath);

            // When
            Set<Entry> result = underTest.getVisibleRootLevelEntries();

            // Then
            assertThat(result)
                    .containsExactly(underTest);
        }
    }

    @DisplayName("isVisible() unit tests")
    @Nested
    class IsVisibleTest {

        @DisplayName("Should throw UncheckedIOException when can't check visibility")
        @Test
        void shouldThrow() {
            // Given
            Path givenPath = Path.of(classpathResource(ROOT_DIR + "/2/1.txt").orElseThrow());
            FileEntry underTest = new FileEntry(givenPath);

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
            Path givenPath = Path.of(classpathResource(ROOT_DIR + "/2/1.txt").orElseThrow());
            FileEntry underTest = new FileEntry(givenPath);

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
            Path givenPath = Path.of(classpathResource(HIDDEN_FILE).orElseThrow());
            FileEntry underTest = new FileEntry(givenPath);

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
        void shouldReturnTest() {
            // Given
            URI givenUri = classpathResource(ROOT_DIR + "/2/1.txt").orElseThrow();
            Path givenPath = Path.of(givenUri);
            FileEntry underTest = new FileEntry(givenPath);

            // When
            Path result = underTest.value();

            // Then
            assertThat(result)
                    .isEqualTo(givenPath);
        }
    }
}