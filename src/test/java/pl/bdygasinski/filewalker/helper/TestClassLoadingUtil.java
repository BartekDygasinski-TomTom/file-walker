package pl.bdygasinski.filewalker.helper;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;

public class TestClassLoadingUtil {

    public static final String ROOT_DIR = "/ROOT";
    public static final String EMPTY_DIR = "/ROOT/EMPTY";
    public static final String HIDDEN_DIR = "/ROOT/.HIDDEN";
    public static final String HIDDEN_FILE = "/ROOT/.hidden";
    public static final String TEXT_FILE = "/ROOT/2/1.txt";
    public static final String HTML_FILE = "/ROOT/index.html";
    public static final String FILE_WITHOUT_EXTENSION = "/ROOT/3/README";

    private TestClassLoadingUtil() {}

    public static Optional<URI> classpathResource(String classpathAbsoluteResourcePath) {
        return Optional
                .ofNullable(TestClassLoadingUtil.class.getResource(classpathAbsoluteResourcePath))
                .flatMap(TestClassLoadingUtil::urlToUri);
    }

    public static Optional<URI> urlToUri(URL url) {
        try {
            return Optional.of(url.toURI());

        } catch (URISyntaxException e) {
            System.err.printf("Invalid URI for resource: %s%n", url);
            return Optional.empty();
        }
    }

    public static Path pathFromClasspath(String classpathAbsoluteResourcePath) {
        return Path.of(classpathResource(classpathAbsoluteResourcePath).orElseThrow());
    }

    public static Path pathWithoutValidation(String notValidPath) {
        return Path.of(notValidPath);
    }
}