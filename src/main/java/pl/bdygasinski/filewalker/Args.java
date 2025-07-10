package pl.bdygasinski.filewalker;

import com.beust.jcommander.Parameter;

public class Args {

    @Parameter(description = "Root path", required = true)
    private String path;

    @Parameter(names = "--max-depth", description = "Max directory depth")
    private int maxDepth = 0;

    public String getPath() {
        return path;
    }

    public int getMaxDepth() {
        return maxDepth;
    }
}