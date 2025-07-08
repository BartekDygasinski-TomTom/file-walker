package pl.bdygasinski.filewalker.helper;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

public class TestClassLoadingUtil {

    public static final String ROOT_DIR = "/ROOT";
    public static final String HIDDEN_DIR = "/.HIDDEN";
    public static final String HIDDEN_FILE = "/.hidden";

    private static final String URI_SYNTAX_EXCEPTION_MSG = "Invalid URI for resource: %s";

    private TestClassLoadingUtil() {
        throw new UnsupportedOperationException("Can't create util class"); //Prevent reflection
    }

    public static Optional<URI> classpathResource(String classpath) {
        return Optional.ofNullable(TestClassLoadingUtil.class.getResource(classpath))
                .flatMap(TestClassLoadingUtil::urlToUri);
    }

    public static Optional<URI> urlToUri(URL url) {
        try {
            return Optional.of(url.toURI());

        } catch (URISyntaxException e) {
            System.err.printf(URI_SYNTAX_EXCEPTION_MSG + "%n", url);
            return Optional.empty();
        }
    }
}
