package pl.bdygasinski.filewalker;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static pl.bdygasinski.filewalker.ClassLoadingUtil.ROOT_DIR;
import static pl.bdygasinski.filewalker.ClassLoadingUtil.classpathResource;

@DisplayName("Entry unit tests")
class EntryTest {


    @DisplayName("FromPathOrThrow unit tests")
    @Nested
    class FormPathOrThrowTest {

        @DisplayName("Should produce FileEntry if path refers to file")
        @Test
        void shouldReturnFileEntry() {
            // Given
            Path givenPath = Path.of(classpathResource(ROOT_DIR + "/2/1.txt").orElseThrow());

            // When
            Entry result = Entry.fromPathOrThrow(givenPath);

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
            Entry result = Entry.fromPathOrThrow(givenPath);

            // Then
            assertThat(result)
                    .isExactlyInstanceOf(DirEntry.class);
        }

        @DisplayName("Should throw IllegalArgumentException when can't produce file or dir entry")
        @Test
        void shouldThrow() {
            // Given
            Path givenPath = null;

            // When
            Exception result = catchException(() -> Entry.fromPathOrThrow(givenPath));

            // Then
            assertThat(result)
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }

}