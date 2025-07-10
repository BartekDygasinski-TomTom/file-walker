package pl.bdygasinski.filewalker.view;

public class FileSizeFormatter {

    private static final String[] UNITS = { "B", "KB", "MB", "GB", "TB" };

    public static String toHumanReadable(long bytes) {
        if (bytes < 1024) return bytes + " B";

        int unitIndex = 0;
        double size = bytes;

        while (size >= 1024 && unitIndex < UNITS.length - 1) {
            size /= 1024.0;
            unitIndex++;
        }

        return String.format("%.1f %s", size, UNITS[unitIndex]);
    }
}