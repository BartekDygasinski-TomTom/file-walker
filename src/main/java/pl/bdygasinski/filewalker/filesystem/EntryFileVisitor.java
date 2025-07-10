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

class EntryFileVisitor implements FileVisitor<Path> {
    private final List<Entry> entries = new ArrayList<>();
    private final int maxDepth;
    private int currDepth = 0;

    public EntryFileVisitor(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        if (currDepth > maxDepth) {
            return FileVisitResult.SKIP_SUBTREE;
        }

        try {
            entries.add(Entry.fromPathAndGraphDepth(dir, currDepth));
        } catch (Exception e) {
            entries.add(new ErrorEntry(currDepth));
        }

        currDepth++;
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        currDepth--;
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        if (currDepth <= maxDepth) {
            try {
                entries.add(Entry.fromPathAndGraphDepth(file, currDepth));
            } catch (Exception e) {
                entries.add(new ErrorEntry(currDepth));
            }
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        entries.add(new ErrorEntry(currDepth));
        return FileVisitResult.CONTINUE;
    }
}

