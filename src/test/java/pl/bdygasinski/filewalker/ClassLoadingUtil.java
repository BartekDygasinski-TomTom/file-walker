package pl.bdygasinski.filewalker;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

class ClassLoadingUtil {

    public static final String ROOT_DIR = "/ROOT";
    public static final String ROOT2_DIR = "/ROOT2";
    public static final String HIDDEN_DIR = "/.HIDDEN";

    private static final String URI_SYNTAX_EXCEPTION_MSG = "Invalid URI for resource: %s";

    private ClassLoadingUtil() {
        throw new UnsupportedOperationException("Can't create util class");
    }

    static Optional<URI> classpathResource(String classpath) {
        return Optional.ofNullable(ClassLoadingUtil.class.getResource(classpath))
                .flatMap(ClassLoadingUtil::urlToUri);
    }

    static Optional<URI> urlToUri(URL url) {
        try {
            return Optional.of(url.toURI());

        } catch (URISyntaxException e) {
            System.err.printf(URI_SYNTAX_EXCEPTION_MSG, url);
            return Optional.empty();
        }
    }
}
