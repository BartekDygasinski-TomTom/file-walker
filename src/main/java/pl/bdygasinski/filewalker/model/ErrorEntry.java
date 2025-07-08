package pl.bdygasinski.filewalker.model;

import java.nio.file.Path;
import java.util.Set;

record ErrorEntry(int depthLevel) implements Entry {

    static ErrorEntry withDefaultDepthLevel() {
        return new ErrorEntry(0);
    }

    @Override
    public String displayName() {
        return "Couldn't get access to that file. ";
    }

    @Override
    public Set<Entry> getRootLevelEntries() {
        return Set.of(this);
    }

    @Override
    public Set<Entry> getVisibleRootLevelEntries() {
        return Set.of(this);
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public Path value() {
        return Path.of("/");
    }
}
