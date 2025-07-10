package pl.bdygasinski.filewalker;

import com.beust.jcommander.Parameter;

public class Args {

    @Parameter(description = "Root path", required = true)
    private String path;

    @Parameter(names = "--name", description = "Partial name match")
    private String name;

    @Parameter(names = "--size", description = "Size range like 1kB-10MB")
    private String size;

    @Parameter(names = "--ext", description = "Comma-separated list of extensions")
    private String ext;

    @Parameter(names = "--max-depth", description = "Max directory depth")
    private int maxDepth = 0;

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public String getExt() {
        return ext;
    }

    public int getMaxDepth() {
        return maxDepth;
    }
}