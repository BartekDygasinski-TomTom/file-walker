package pl.bdygasinski.filewalker;

import com.beust.jcommander.JCommander;
import pl.bdygasinski.filewalker.filesystem.EntriesProvider;
import pl.bdygasinski.filewalker.filesystem.EntryFileVisitor;
import pl.bdygasinski.filewalker.model.Entry;
import pl.bdygasinski.filewalker.view.ContentVisualizer;
import pl.bdygasinski.filewalker.view.DisplayableEntry;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static java.util.Objects.nonNull;

public class Main {

    private static Args parsedArgs = new Args();

    public static void main(String... notParsedArgs) {
        var jcommander = setUpJCommander(notParsedArgs);

        var filter = prepareCombinedFilterFromArgs(parsedArgs);
        var visitor = new EntryFileVisitor(parsedArgs.getMaxDepth(), filter
        );
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

    static Predicate<Entry> prepareCombinedFilterFromArgs(Args args) {
        List<Predicate<Entry>> filters = new ArrayList<>();

        if (nonNull(args.getName())) {
            Predicate<Entry> newFilter = entry -> entry.baseName().contains(args.getName());
            filters.add(newFilter);
        }

        if (nonNull(args.getExt())) {
            Predicate<Entry> newFilter = entry -> entry.fileExtension()
                    .map(extension -> extension.equals(args.getExt()))
                    .orElse(false);
            filters.add(newFilter);
        }


        return filters
                .stream()
                .reduce(entry -> true, Predicate::and);
    }



}
