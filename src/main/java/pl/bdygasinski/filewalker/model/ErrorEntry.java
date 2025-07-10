package pl.bdygasinski.filewalker.model;

public record ErrorEntry(int depthLevel) implements Entry {

    @Override
    public String baseName() {
        return "Unaccessible entry";
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
