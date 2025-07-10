package pl.bdygasinski.filewalker;

import com.beust.jcommander.JCommander;
import pl.bdygasinski.filewalker.filesystem.EntriesProvider;
import pl.bdygasinski.filewalker.filesystem.EntryFileVisitor;
import pl.bdygasinski.filewalker.model.Entry;
import pl.bdygasinski.filewalker.view.ContentVisualizer;
import pl.bdygasinski.filewalker.view.DisplayableEntry;

import java.nio.file.Path;
import java.util.List;

public class Main {

    private static Args parsedArgs = new Args();

    public static void main(String... notParsedArgs) {
        var jcommander = setUpJCommander(notParsedArgs);

        var visitor = new EntryFileVisitor(parsedArgs.getMaxDepth(), Entry::isVisible);
        var provider = EntriesProvider.withVisitor(visitor);
        var entries = extractEntriesFromProvider(parsedArgs, provider);
        var visualizer = ContentVisualizer.forEntries(entries);

        visualizer.listVisible();
    }

    private static List<DisplayableEntry> extractEntriesFromProvider(Args parsedArgs, EntriesProvider entriesProvider) {
        Path path = Path.of(parsedArgs.getPath());

        return entriesProvider.getEntriesFromPath(path)
                .stream()
                .map(DisplayableEntry::new)
                .toList();
    }

    private static JCommander setUpJCommander(String[] notParsedArgs) {
        JCommander commander = JCommander
                .newBuilder()
                .addObject(parsedArgs)
                .build();

        commander.parse(notParsedArgs);
        return commander;
    }
}
