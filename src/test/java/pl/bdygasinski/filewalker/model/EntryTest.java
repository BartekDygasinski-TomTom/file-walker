package pl.bdygasinski.filewalker.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import pl.bdygasinski.filewalker.exception.EntryNotAccessibleException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static pl.bdygasinski.filewalker.helper.TestClassLoadingUtil.ROOT_DIR;
import static pl.bdygasinski.filewalker.helper.TestClassLoadingUtil.classpathResource;

class EntryTest {


    @DisplayName("fromPath() unit tests")
    @Nested
    class FromPathTest {

        @DisplayName("Should produce FileEntry if path refers to file")
        @Test
        void shouldReturnFileEntry() {
            // Given
            Path givenPath = Path.of(classpathResource(ROOT_DIR + "/2/1.txt").orElseThrow());

            // When
            Entry result = Entry.fromPath(givenPath);

            // Then
            assertThat(result)
                    .isExactlyInstanceOf(FileEntry.class);
        }

        @DisplayName("Should produce DirEntry if path refers to dir")
        @Test
        void shouldReturnDirEntry() {
            // Given
            Path givenPath = Path.of(classpathResource(ROOT_DIR).orElseThrow());

            // When
            Entry result = Entry.fromPath(givenPath);

            // Then
            assertThat(result)
                    .isExactlyInstanceOf(DirEntry.class);
        }

        @DisplayName("Should produce ErrorEntry when can't produce file or dir entry")
        @Test
        void shouldReturnErrorEntry() {
            // Given
            Path givenPath = null;

            // When
            Entry result = Entry.fromPath(givenPath);

            // Then
            assertThat(result)
                    .isExactlyInstanceOf(ErrorEntry.class);
        }
    }

    @DisplayName("isVisible() unit tests")
    @Nested
    class IsVisibleTest {

        @DisplayName("Should return true if file isn't hidden")
        @Test
        void shouldReturnTrueIfFileIsntHidden() {
            // Given
            Path givenPath = Path.of(classpathResource(ROOT_DIR).orElseThrow());
            Entry underTest = Entry.fromPath(givenPath);

            try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
                mockedFiles
                        .when(() -> Files.isHidden(givenPath))
                        .thenReturn(false);

                // When
                boolean result = underTest.isVisible();

                // Then
                assertThat(result)
                        .isTrue();
            }
        }

        @DisplayName("Should throw if file is hidden")
        @Test
        void shouldThrowIfFileIsHidden() {
            // Given
            Path givenPath = Path.of(classpathResource(ROOT_DIR).orElseThrow());
            Entry underTest = Entry.fromPath(givenPath);

            try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
                mockedFiles
                        .when(() -> Files.isHidden(givenPath))
                        .thenThrow(new IOException("Access denied"));

                // When Then
               assertThrows(EntryNotAccessibleException.class, underTest::isVisible);
            }
        }
    }
}