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


    public static long parseSize(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Size string cannot be null or blank");
        }

        String normalized = input.trim().toUpperCase().replaceAll("[ ]", "");

        long multiplier = 1;

        if (normalized.endsWith("KB")) {
            multiplier = 1024L;
            normalized = normalized.replace("KB", "");
        } else if (normalized.endsWith("MB")) {
            multiplier = 1024L * 1024L;
            normalized = normalized.replace("MB", "");
        } else if (normalized.endsWith("GB")) {
            multiplier = 1024L * 1024L * 1024L;
            normalized = normalized.replace("GB", "");
        } else if (normalized.endsWith("TB")) {
            multiplier = 1024L * 1024L * 1024L * 1024L;
            normalized = normalized.replace("TB", "");
        } else if (normalized.endsWith("B")) {
            normalized = normalized.replace("B", "");
        }

        long value = Long.parseLong(normalized);
        return value * multiplier;
    }
}