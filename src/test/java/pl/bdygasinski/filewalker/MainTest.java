package pl.bdygasinski.filewalker;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static pl.bdygasinski.filewalker.Main.main;

class MainTest {

    @DisplayName("Should throw IllegalArgumentException for invalid args")
    @ParameterizedTest
    @MethodSource("invalidArgsProvider")
    void mainShouldThrowIfArgsInvalid(String[] args) {
        // When
        Exception result = catchException(() -> main(args));

        // Then
        assertThat(result)
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<Arguments> invalidArgsProvider() {
        return Stream.of(
                Arguments.of((Object) null), // null args
                Arguments.of((Object) new String[] {})  // empty args
        );
    }
}