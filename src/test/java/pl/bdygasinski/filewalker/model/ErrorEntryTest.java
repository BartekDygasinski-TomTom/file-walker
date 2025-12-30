package pl.bdygasinski.filewalker.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.bdygasinski.filewalker.helper.TestClassLoadingUtil.pathWithoutValidation;

class ErrorEntryTest {

    @DisplayName("baseName()")
    @Nested
    class BaseNameTest {

        @DisplayName("Should return empty string")
        @Test
        void baseName() {
            // Given
            var givenPath = Path.of("/example/file");
            var givenDepth = 0;
            var underTest = new ErrorEntry(givenDepth, givenPath);

            // When
            String result = underTest.baseName();

            // Then
            assertThat(result)
                    .isEmpty();
        }
    }

    @DisplayName("isVisible()")
    @Nested
    class IsVisibleTest {

        @DisplayName("Should always return true for any input data")
        @Test
        void isVisible() {
            // Given
            var givenPath = pathWithoutValidation("incorrect");
            var underTest = new ErrorEntry(0, givenPath);

            // When
            boolean result = underTest.isVisible();

            // Then
            assertThat(result)
                    .isTrue();
        }
    }

    @DisplayName("depthLevel()")
    @Nested
    class DepthLevelTest {

        @DisplayName("Should return same depth level from input when depth is not negative")
        @ParameterizedTest(name = "When depthLevel={0} return value should be {1}")
        @ValueSource(ints = {0, 1, 2, Integer.MAX_VALUE})
        void shouldBeSameWhenPositive(int depthLevel) {
            // Given
            var givenPath = pathWithoutValidation("incorrect");
            var underTest = new ErrorEntry(depthLevel, givenPath);

            // When
            int result = underTest.depthLevel();

            // Then
            assertThat(result)
                    .isEqualTo(depthLevel);
        }

        @DisplayName("Should return 0 when depth level is negative")
        @ParameterizedTest(name = "When depthLevel={0} return value should be 0")
        @ValueSource(ints = {Integer.MIN_VALUE, -2, -1})
        void shouldBe0WhenNegative(int depthLevel) {
            // Given
            var givenPath = pathWithoutValidation("incorrect");
            var underTest = new ErrorEntry(depthLevel, givenPath);

            // When
            int result = underTest.depthLevel();

            // Then
            assertThat(result)
                    .isEqualTo(0);
        }
    }

    @DisplayName("path()")
    @Nested
    class PathTest {

        @DisplayName("Should return input")
        @Test
        void path() {
            // Given
            var givenPath = Path.of("");
            var givenDepth = 0;
            var underTest = new ErrorEntry(givenDepth, givenPath);

            // When
            Path result = underTest.path();

            // Then
            assertThat(result)
                    .isEqualTo(givenPath);
        }
    }
}