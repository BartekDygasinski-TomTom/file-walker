package pl.bdygasinski.filewalker.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pl.bdygasinski.filewalker.helper.TestClassLoadingUtil;
import pl.bdygasinski.filewalker.model.Entry;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.bdygasinski.filewalker.helper.TestClassLoadingUtil.*;

class ConsoleContentVisualizerTest {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
    private final PrintStream out = new PrintStream(outputStream);
    private final PrintStream err  = new PrintStream(errorStream);

    private static final String NL = System.lineSeparator();

    @BeforeEach
    void setUp() {
        System.setOut(out);
        System.setErr(err);
    }

    @DisplayName("listVisible()")
    @Nested
    class ListVisibleTest {

        @DisplayName("Should print one entry name per line")
        @Test
        void listVisible() {
            // Given
            var givenEntries = Stream.of(TEXT_FILE, ROOT_DIR, HTML_FILE)
                    .map(TestClassLoadingUtil::pathFromClasspath)
                    .map(path -> Entry.fromPathAndGraphDepth(path, 0))
                    .map(DisplayableEntry::new)
                    .toList();
            var underTest = new ConsoleContentVisualizer(givenEntries);

            // When
            underTest.listVisible();

            // Then
            var expected = givenEntries
                    .stream()
                    .map(DisplayableEntry::entryName)
                    .collect(Collectors.joining(NL, "", NL));

            assertThat(outputStream.toString())
                    .isEqualTo(expected);

        }
    }
}