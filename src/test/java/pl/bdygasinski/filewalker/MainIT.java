package pl.bdygasinski.filewalker;

import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class MainIT {

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


}