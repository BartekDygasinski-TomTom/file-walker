package pl.bdygasinski.filewalker;

import com.beust.jcommander.JCommander;
import pl.bdygasinski.filewalker.filesystem.EntriesProvider;
import pl.bdygasinski.filewalker.model.Entry;
import pl.bdygasinski.filewalker.view.ContentVisualizer;
import pl.bdygasinski.filewalker.view.DisplayableEntry;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static java.util.Objects.nonNull;

interface AppApi {

    static void main(String[] args) {
        var parsedArgs = new Args();
        var jcommander = JCommander
                .newBuilder()
                .addObject(parsedArgs)
                .build();
        jcommander.parse(args);

        if (parsedArgs.help()) {
            jcommander.usage();
            System.exit(0);
        }


        var displayableContent = getContent(parsedArgs);
        var visualizer = ContentVisualizer.getDefault(displayableContent);
        visualizer.listVisible();
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

    static List<DisplayableEntry> getContent(Args args) {
        var contentProvider = EntriesProvider.withFilter(prepareCombinedFilterFromArgs(args));
        var content = contentProvider.getEntriesFromPath(Path.of(args.getPath()), args.getMaxDepth());
        return content.stream()
                .map(DisplayableEntry::new)
                .toList();
    }

}
