package pl.bdygasinski.filewalker;

import java.nio.file.Path;

import static java.util.Objects.isNull;

interface Main {

    static void main(String[] args) {
        validateInput(args);
        var provider = ContentProvider.getInstance();
        var visualiser = ContentVisualizer.getInstance(provider);

        visualiser.listVisible(Path.of(args[0]));
    }

    private static void validateInput(String[] args) {
        if (isNull(args) || args.length == 0) {
            throw new IllegalArgumentException("At least one arg must be provided");
        }
    }
}
