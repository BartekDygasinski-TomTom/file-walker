package pl.bdygasinski.filewalker.model;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class DisplayName {

    private static final int INDENTATION_SPACES = 4;

    private final String basicName;
    private final String indentation;

    private DisplayName(String basicName, String indentation) {
        this.basicName = requireNonNull(basicName);
        this.indentation = requireNonNull(indentation);
    }

    public static DisplayName withNameAndDepthLevel(String name, int depthLevel) {
        if (depthLevel < 0) {
            depthLevel = 0;
        }

        String indent = " ".repeat(depthLevel * INDENTATION_SPACES);
        return new DisplayName(name, indent);
    }

    public String name() {
        return basicName;
    }

    public String indentation() {
        return indentation;
    }

    public String nameWithIndentation() {
        return indentation + basicName;
    }

    @Override
    public String toString() {
        return nameWithIndentation();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DisplayName that)) return false;
        return Objects.equals(basicName, that.basicName) && Objects.equals(indentation, that.indentation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(basicName, indentation);
    }
}
