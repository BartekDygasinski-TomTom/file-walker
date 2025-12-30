package pl.bdygasinski.filewalker.view;

import java.util.Optional;

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

    public static Optional<Long> toBytes(String humanReadableValue) {
        try {
            String[] data = humanReadableValue.trim().split("\\s+");
            if (data.length != 2) return Optional.empty();

            long value = Long.parseLong(data[0]);
            String unit = data[1].toUpperCase();

            Optional<Long> multiplyValue = switch (unit) {
                case "B" -> Optional.of(1L);
                case "KB" -> Optional.of(1024L);
                case "MB" -> Optional.of(1024L * 1024L);
                case "GB" -> Optional.of(1024L * 1024L * 1024L);
                case "TB" -> Optional.of(1024L * 1024L * 1024L * 1024L);
                default -> Optional.empty();
            };

            return multiplyValue.map(unitValue -> unitValue * value);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}