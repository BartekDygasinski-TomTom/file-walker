package pl.bdygasinski.filewalker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.BDDMockito.given;
import static pl.bdygasinski.filewalker.ClassLoadingUtil.ROOT_DIR;
import static pl.bdygasinski.filewalker.ClassLoadingUtil.classpathResource;

@DisplayName("ConsoleContentVisualizer unit tests")
@ExtendWith(MockitoExtension.class)
class ConsoleContentVisualizerTest {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errorStream = new ByteArrayOutputStream();

    @Mock
    private ContentProvider contentProvider;

    private ContentVisualizer underTest;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStream));
        System.setErr(new PrintStream(errorStream));
        underTest = ContentVisualizer.getInstance(contentProvider);
    }

    @DisplayName("showRoot() unit tests")
    @Nested
    class ShowRootTest {

        @DisplayName("Should display content")
        @Test
        void should_println_content() {
            // Given
            Path givenRootDirPath = Path.of(classpathResource(ROOT_DIR).orElseThrow());
            DirEntry givenEntry2 = new DirEntry(givenRootDirPath);

            Path givenFilePath = Path.of(classpathResource(ROOT_DIR + "/2/1.txt").orElseThrow());
            FileEntry givenEntry1 = new FileEntry(givenFilePath);

            given(contentProvider.provideEntriesFrom(givenRootDirPath))
                    .willReturn(Set.of(givenEntry1, givenEntry2));

            // When
            underTest.listVisible(givenRootDirPath);

            // Then
            assertThat(outputStream.toString())
                    .contains(Set.of(givenEntry1.displayName(), givenEntry2.displayName()));
            
        }

        @DisplayName("Should require non null Path")
        @Test
        void should_require_non_null_path() {
            // Given
            Path givenPath = null;

            // When
            Exception result = catchException(() -> underTest.listVisible(givenPath));

            // Then
            assertThat(result)
                    .isNotNull();
        }
    }
}