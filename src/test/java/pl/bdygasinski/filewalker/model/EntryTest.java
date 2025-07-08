package pl.bdygasinski.filewalker.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
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

}