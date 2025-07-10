package pl.bdygasinski.filewalker.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pl.bdygasinski.filewalker.model.DirEntry;
import pl.bdygasinski.filewalker.model.ErrorEntry;
import pl.bdygasinski.filewalker.model.FileEntry;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.bdygasinski.filewalker.helper.TestClassLoadingUtil.*;

class DisplayableEntryTest {

    @DisplayName("entryName()")
    @Nested
    class EntryName {

        @DisplayName("Should display error entry with error message and path for unaccessible entry")
        @Test
        void shouldDisplayErrorWithPathForErrorEntry() {
            // Given
            var givenPath = pathWithoutValidation("incorrect");
            var givenEntry = new ErrorEntry(0, givenPath);
            var underTest = new DisplayableEntry(givenEntry);

            // When
            String result = underTest.entryName();

            // Then
            assertThat(result)
                    .isEqualTo("%s %s".formatted(DisplayableEntry.ERROR_ENTRY_DISPLAY_NAME_PREFIX, givenEntry.path()));
        }

        @DisplayName("Should display file entry as file base name")
        @Test
        void shouldDisplayFileEntryAsFileBaseName() {
            // Given
            var givenPath = pathFromClasspath(TEXT_FILE);
            var givenEntry = FileEntry.fromPathAndDepthLevel(givenPath, 0);
            var underTest = new DisplayableEntry(givenEntry);

            // When
            String result = underTest.entryName();

            // Then
            assertThat(result)
                    .isEqualTo(givenEntry.baseName());
        }

        @DisplayName("Should display dir entry with [dir] prefix and directory base name")
        @Test
        void shouldDisplayDirectoryWithDirPrefix() {
            // Given
            var givenPath = pathFromClasspath(ROOT_DIR);
            var givenEntry = DirEntry.fromPathAndDepthLevel(givenPath, 0);
            var underTest = new DisplayableEntry(givenEntry);

            // When
            String result = underTest.entryName();

            // Then
            assertThat(result)
                    .isEqualTo("%s %s".formatted(DisplayableEntry.DIR_ENTRY_DISPLAY_NAME_PREFIX, givenEntry.baseName()));
        }
    }
}