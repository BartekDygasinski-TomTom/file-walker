package pl.bdygasinski.filewalker.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static pl.bdygasinski.filewalker.helper.TestClassLoadingUtil.*;

class FileEntryTest {

    @DisplayName("constructor()")
    @Nested
    class ConstructorTest {

        @DisplayName("Should throw if path does not refer to file")
        @Test
        void shouldThrowIfPathDoesNotReferToFile() {
            // Given
            var givenPath = pathFromClasspath(ROOT_DIR);

            // When
            var result = catchException(() -> FileEntry.fromPathAndDepthLevel(givenPath, 0));

            // Then
            assertThat(result)
                    .isNotNull()
                    .hasMessageContaining(givenPath.toString());
        }

        @DisplayName("Should throw if depthLevel is negative")
        @ParameterizedTest(name = "When input={0}, constructor should throw")
        @ValueSource(ints = {Integer.MIN_VALUE, -1})
        void shouldThrowIfDepthLevelIsNegative(int depthLevel) {
            // Given
            var givenPath = pathFromClasspath(TEXT_FILE);

            // When
            var result = catchException(() -> FileEntry.fromPathAndDepthLevel(givenPath, depthLevel));

            // Then
            assertThat(result)
                    .isNotNull()
                    .hasMessageContaining(String.valueOf(depthLevel));
        }
    }

    @DisplayName("baseName()")
    @Nested
    class BaseNameTest {

        @DisplayName("Should return base name of the input path")
        @Test
        void shouldReturnBaseNameOfTheInputPath() {
            // Given
            var givenPath = pathFromClasspath(TEXT_FILE);
            var underTest = FileEntry.fromPathAndDepthLevel(givenPath, 0);

            // When
            var result = underTest.baseName();

            // Then
            assertThat(result)
                    .isEqualTo(givenPath.getFileName().toString());
        }
    }

    @DisplayName("depthLevel()")
    @Nested
    class DepthLevelTest {

        @DisplayName("Should return same depthLevel from the input")
        @Test
        void shouldReturnSameDepthLevelFromTheInput() {
            // Given
            var givenPath = pathFromClasspath(TEXT_FILE);
            var givenDepthLevel = 0;
            var underTest = FileEntry.fromPathAndDepthLevel(givenPath, givenDepthLevel);

            // When
            var result = underTest.depthLevel();

            // Then
            assertThat(result)
                    .isEqualTo(givenDepthLevel);
        }
    }

    @DisplayName("isVisible()")
    @Nested
    class IsVisibleTest {

        @DisplayName("Should return isVisible from the input")
        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void shouldReturnBaseNameOfTheInputPath(boolean isVisible) {
            // Given
            var givenPath = pathFromClasspath(TEXT_FILE);
            var underTest = new FileEntry(givenPath, 0, isVisible);

            // When
            var result = underTest.isVisible();

            // Then
            assertThat(result)
                    .isEqualTo(isVisible);
        }
    }
}