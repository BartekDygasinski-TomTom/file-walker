package pl.bdygasinski.filewalker;

import com.beust.jcommander.Parameter;

public class Args {

    @Parameter(description = "Root path", required = true)
    private String path;

    public String getPath() {
        return path;
    }
}