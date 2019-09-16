package org.michael.common;

import org.apache.commons.cli.*;

/**
 * Created on 2019-09-16 11:15
 * Author : Michael.
 */
public class CommonLineParameters {

    final Options options;
    final CommandLine commandLine;

    public CommonLineParameters(Options options, String[] mainArgs) {
        this.options = options;
        this.commandLine = parseArgs(mainArgs);
    }

    private CommandLine parseArgs(String[] args) {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(options, args);
            return line;
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("c", options);
            throw new RuntimeException("Parse args failed.", e);
        }
    }

    public String getString(String key) {
        String value = commandLine.getOptionValue(key);
        return value;
    }

    public int getInt(String key, int defaultValue) {
        String value = getString(key);
        return value == null ? defaultValue : Integer.parseInt(value);
    }
}
