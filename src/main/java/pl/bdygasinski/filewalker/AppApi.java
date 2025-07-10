package pl.bdygasinski.filewalker;

import com.beust.jcommander.JCommander;
import pl.bdygasinski.filewalker.filesystem.EntriesProvider;
import pl.bdygasinski.filewalker.view.ContentVisualizer;
import pl.bdygasinski.filewalker.view.DisplayableEntry;

import java.nio.file.Path;

interface AppApi {

    static void main(String[] args) {
        Args parsedArgs = new Args();
        JCommander.newBuilder()
                .addObject(parsedArgs)
                .build()
                .parse(args);

        var contentProvider = EntriesProvider.getDefault();
        var content = contentProvider.getEntriesFromPath(Path.of(parsedArgs.getPath()), parsedArgs.getMaxDepth());
        var displayableContent = content.stream()
                .map(DisplayableEntry::new)
                .toList();

        var visualizer = ContentVisualizer.getDefault(displayableContent);
        visualizer.listVisible();
    }

}
