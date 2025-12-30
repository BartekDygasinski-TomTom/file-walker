package pl.bdygasinski.filewalker.filesystem;

import pl.bdygasinski.filewalker.model.Entry;
import pl.bdygasinski.filewalker.model.ErrorEntry;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

public class EntryFileVisitor implements FileVisitor<Path> {
    private final List<Entry> entries;
    private final int maxDepth;
    private final Predicate<Entry> filter;
    private int currDepth;
    private boolean isRooVisited = false;

    public EntryFileVisitor(int maxDepth, Predicate<Entry> filter) {
        this(maxDepth, filter, new ArrayList<>());
    }

    public EntryFileVisitor(int maxDepth, Predicate<Entry> filter, List<Entry> startList) {
        if (maxDepth < 0) {
            throw new IllegalArgumentException("Max depth must be positive but got %s".formatted(maxDepth));
        }

        this.filter = requireNonNull(filter, "Filter is required but bot %s".formatted(filter));
        this.maxDepth = maxDepth;
        this.entries = new ArrayList<>(startList);
        this.currDepth = 0;
    }

    public List<Entry> getEntries() {
        return List.copyOf(entries);
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dirPath, BasicFileAttributes attrs) {
        if (currDepth > maxDepth) {
            return FileVisitResult.SKIP_SUBTREE;
        }

        if (!isRooVisited && maxDepth == 0 && currDepth == maxDepth) {
            isRooVisited = true;
            return FileVisitResult.CONTINUE;
        }

        Entry entry = Entry.fromPathAndGraphDepth(dirPath, currDepth);
        if (entry.isVisible()) {
            entries.add(entry);
        }

        incrementCurrDepth();
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dirPath, IOException exc) {
        decrementCurrDepth();
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) {
        if (currDepth <= maxDepth) {
            Entry entry = Entry.fromPathAndGraphDepth(filePath, currDepth);
            if (filter.test(entry) && entry.isVisible()) {
                entries.add(entry);

            }
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path filePath, IOException exc) {
        entries.add(new ErrorEntry(currDepth, filePath));
        return FileVisitResult.CONTINUE;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    protected int getCurrDepth() {
        return currDepth;
    }

    protected int incrementCurrDepth() {
        return ++currDepth;
    }

    protected int decrementCurrDepth() {
        return --currDepth;
    }
}