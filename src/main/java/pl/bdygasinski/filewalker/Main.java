package pl.bdygasinski.filewalker;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import pl.bdygasinski.filewalker.model.Entry;
import pl.bdygasinski.filewalker.validator.PositiveIntegerValidator;

import java.nio.file.Path;
import java.util.Set;

import static java.util.Objects.nonNull;

class Main {

    public static void main(String[] args) {

        var appArgs = setUpArgs(args);

        var provider = ContentProvider.getInstance();
        Set<Entry> entries = provideEntriesDependingOnOptions(appArgs, provider);

        var visualiser = ContentVisualizer.withEntries(entries);
        visualiser.listVisible();
    }

    private static class AppArgs {

        @Parameter(description = "Path to explore", required = true)
        private String pathString;

        @Parameter(
                names = {"--max-depth", "-R"},
                description = "Max tree depth to display (enables tree view)",
                validateWith = PositiveIntegerValidator.class
        )
        public Integer maxDepth = null;
    }

    private static Set<Entry> provideEntriesDependingOnOptions(AppArgs args, ContentProvider contentProvider) {
        Path path = Path.of(args.pathString);

        if (nonNull(args.maxDepth)) {
            return contentProvider.provideEntriesRecursivelyFrom(path, args.maxDepth);
        }

        return contentProvider.provideEntriesFrom(path);
    }

    private static AppArgs setUpArgs(String[] args) {
        AppArgs appArgs = new AppArgs();
        JCommander
                .newBuilder()
                .addObject(appArgs)
                .build()
                .parse(args);

        return appArgs;
    }
}
