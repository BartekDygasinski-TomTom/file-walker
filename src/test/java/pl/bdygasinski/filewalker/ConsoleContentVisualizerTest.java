package pl.bdygasinski.filewalker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pl.bdygasinski.filewalker.model.Entry;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static pl.bdygasinski.filewalker.helper.TestClassLoadingUtil.ROOT_DIR;
import static pl.bdygasinski.filewalker.helper.TestClassLoadingUtil.classpathResource;

class ConsoleContentVisualizerTest {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errorStream = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStream));
        System.setErr(new PrintStream(errorStream));
    }

    @DisplayName("showRoot() unit tests")
    @Nested
    class ShowRootTest {

        @DisplayName("Should display content")
        @Test
        void shouldPrintLnContent() {
            // Given
            Path givenRootDirPath = Path.of(classpathResource(ROOT_DIR).orElseThrow());
            Entry givenEntry2 = Entry.fromPath(givenRootDirPath);

            Path givenFilePath = Path.of(classpathResource(ROOT_DIR + "/2/1.txt").orElseThrow());
            Entry givenEntry1 = Entry.fromPath(givenFilePath);

            Set<Entry> givenData = Set.of(givenEntry1, givenEntry2);
            ContentVisualizer underTest = ContentVisualizer.withEntries(givenData);

            // When
            underTest.listVisible();

            // Then
            assertThat(outputStream.toString())
                    .contains(Set.of(givenEntry1.displayName(), givenEntry2.displayName()));
            
        }

        @DisplayName("Should require non null entries")
        @Test
        void shouldRequireNonNullPath() {
            // Given
            Set<Entry> entries = null;

            // When
            Exception result = catchException(() -> new ConsoleContentVisualizer(entries));

            // Then
            assertThat(result)
                    .isNotNull();
        }
    }
}