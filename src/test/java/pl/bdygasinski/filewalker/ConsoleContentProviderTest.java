package pl.bdygasinski.filewalker;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pl.bdygasinski.filewalker.Entry.DirEntry;
import pl.bdygasinski.filewalker.Entry.FileEntry;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.bdygasinski.filewalker.ClassLoadingUtil.ROOT_DIR;
import static pl.bdygasinski.filewalker.ClassLoadingUtil.classpathResource;

@DisplayName("ConsoleContentProvider unit tests")
class ConsoleContentProviderTest {

    private final ContentProvider underTest = ContentProvider.getInstance();

    @DisplayName("provideEntries() unit tests")
    @Nested
    class ProvideEntriesTest {

        @DisplayName("Should return file entry if path refers to regular file")
        @Test
        void should_detect_file() {
            // Given
            FileEntry givenFileEntry = new FileEntry("1.txt");
            URI givenUri = classpathResource("%s/2/%s".formatted(ROOT_DIR, givenFileEntry.value())).orElseThrow();
            Path givenPath = Path.of(givenUri);

            // When
            List<Entry> result = underTest.provideEntries(givenPath);

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
            List<Entry> result = underTest.provideEntries(givenPath);

            // Then
            assertThat(result)
                    .hasSizeGreaterThan(1)
                    .doesNotContain(givenDir);
        }
    }
}