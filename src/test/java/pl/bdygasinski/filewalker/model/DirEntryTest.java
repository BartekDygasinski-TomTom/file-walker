package pl.bdygasinski.filewalker.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static pl.bdygasinski.filewalker.helper.TestClassLoadingUtil.*;

class DirEntryTest {

    @DisplayName("constructor()")
    @Nested
    class ConstructorTest {

        @DisplayName("Should throw if base name is null")
        @Test
        void shouldThrowIfBaseNameIsNull() {
            // Given + When
            var givenPath = pathFromClasspath(ROOT_DIR);
            var result = catchException(() -> new DirEntry(null, 0, true, givenPath));

            // Then
            assertThat(result)
                    .isNotNull()
                    .hasMessageContaining("null");
        }

        @DisplayName("Should throw if depth level is negative")
        @Test
        void shouldThrowIfDepthLevelIsNegative() {
            // Given + When
            var givenDepthLevel = -1;
            var givenPath = pathWithoutValidation("incorrect");
            var result = catchException(() -> new DirEntry("", givenDepthLevel, true, givenPath));

            // Then
            assertThat(result)
                    .isNotNull()
                    .message()
                    .contains(String.valueOf(givenDepthLevel));
        }

        @DisplayName("Should throw if path doesn't refer to directory")
        @Test
        void shouldThrowIfPathDoesntReferDirectory() {
            // Given + When
            var givenNotDirPath = pathFromClasspath(HTML_FILE);
            var result = catchException(() -> new DirEntry("example", 0, true, givenNotDirPath));

            // Then
            assertThat(result)
                    .isNotNull()
                    .message()
                    .contains(givenNotDirPath.toString());
        }
    }

    @DisplayName("baseName()")
    @Nested
    class BaseNameTest {

        @DisplayName("Should be the same as an input")
        @ParameterizedTest(name = "When baseName={0} output should be {0}")
        @ValueSource(strings = {"", "123456789", "!@#$%^&*()_+", ".", ".a"})
        void shouldBeTheSameAsAnInput(String baseName) {
            // Given
            var underTest = new DirEntry(baseName, 0, true, pathFromClasspath(""));

            // When
            String result = underTest.baseName();

            // Then
            assertThat(result)
                    .isEqualTo(baseName);
        }
    }

    @DisplayName("depthLevel()")
    @Nested
    class DepthLevelTest {

        @DisplayName("Should be the same as an input")
        @ParameterizedTest(name = "When depthLevel={0} output should be {0}")
        @ValueSource(ints = {0, Integer.MAX_VALUE})
        void shouldBeTheSameAsAnInput(int depthLevel) {
            // Given
            var underTest = new DirEntry("baseName", depthLevel, true, pathFromClasspath(""));

            // When
            var result = underTest.depthLevel();

            // Then
            assertThat(result)
                    .isEqualTo(depthLevel);
        }
    }

    @DisplayName("isVisible()")
    @Nested
    class IsVisibleTest {

        @DisplayName("Should be the same as an input")
        @ParameterizedTest(name = "When input={0} output should be {0}")
        @ValueSource(booleans = {true, false})
        void shouldBeTheSameAsAnInput(boolean isVisible) {
            // Given
            var underTest = new DirEntry("", 0, isVisible, pathFromClasspath(""));

            // When
            var result = underTest.isVisible();

            // Then
            assertThat(result)
                    .isEqualTo(isVisible);
        }
    }

    @DisplayName("path()")
    @Nested
    class PathTest {

        @DisplayName("Should be the same as an input")
        @Test
        void shouldBeTheSameAsAnInput() {
            // Given
            var givenPath = pathFromClasspath("");
            var underTest = new DirEntry("baseName", 0, true, givenPath);

            // When
            var result = underTest.path();

            // Then
            assertThat(result)
                    .isEqualTo(givenPath);
        }
    }
}