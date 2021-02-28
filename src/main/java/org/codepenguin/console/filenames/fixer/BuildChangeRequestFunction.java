/*
 * MIT License
 *
 * Copyright (c) 2021 Jorge Garcia - codepenguin.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package org.codepenguin.console.filenames.fixer;

import lombok.extern.java.Log;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.function.Function;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.logging.Level.FINE;
import static org.apache.commons.lang3.ArrayUtils.addAll;
import static org.apache.commons.lang3.ArrayUtils.isEmpty;

/**
 * Function to build a change request from the command arguments.
 *
 * @author Jorge Garcia
 * @version 1.0.0
 * @since 1.8
 */
@Log
class BuildChangeRequestFunction implements Function<String[], ProcessResponse<ChangeRequest>> {

    private static final String FILE_OPTION = "f";
    private static final String APPLY_TO_HIDDEN_OPTION = "a";
    private static final String HELP_OPTION = "h";
    private static final String CMD_LINE_SYNTAX = "java -jar my-filenames-fixer.jar";
    private static final String HEADER = "My Filenames Fixer";
    private static final String FOOTER = "v1.0.0";
    private static final int WIDTH = 800;
    private static final int LEFT_PAD = 5;
    private static final int DESC_PAD = 5;

    @Override
    public ProcessResponse<ChangeRequest> apply(final String[] args) {
        if (isEmpty(args)) {
            return new ProcessResponse<>(FALSE, "There isn't arguments.");
        }

        final Options noRequiredOptions = buildOptions(addAll(buildAppOptions(TRUE), buildHelpOptions()));
        final CommandLine helpCommandLine;
        try {
            helpCommandLine = new DefaultParser().parse(noRequiredOptions, args);
        } catch (ParseException e) {
            log.log(FINE, Arrays.toString(args), e);
            return new ProcessResponse<>(FALSE, e.getMessage());
        }

        if (helpCommandLine.hasOption(HELP_OPTION)) {
            return new ProcessResponse<>(TRUE, getHelp(noRequiredOptions));
        }

        final CommandLine commandLine;
        try {
            commandLine = new DefaultParser().parse(buildOptions(buildAppOptions(FALSE)), args);
        } catch (ParseException e) {
            log.log(FINE, Arrays.toString(args), e);
            return new ProcessResponse<>(FALSE, e.getMessage());
        }

        final File file = new File(commandLine.getOptionValue(FILE_OPTION));
        final ChangeRequestOptions requestOptions = ChangeRequestOptions.builder()
                .applyToHidden(commandLine.hasOption(APPLY_TO_HIDDEN_OPTION)).build();
        final ChangeRequest changeRequest = new ChangeRequest(file, requestOptions);
        return new ProcessResponse<>(TRUE, String.valueOf(changeRequest), changeRequest);
    }

    private Options buildOptions(final Option... theOptions) {
        final Options options = new Options();
        Arrays.stream(theOptions).forEach(options::addOption);
        return options;
    }

    private Option[] buildAppOptions(boolean allNoRequired) {
        return new Option[]{
                Option.builder(FILE_OPTION).longOpt("file").hasArg(!allNoRequired)
                        .desc("The file or directory to change.").required(!allNoRequired).build(),
                Option.builder(APPLY_TO_HIDDEN_OPTION).longOpt("apply-to-hidden").hasArg(FALSE)
                        .desc("Indicates if the process must apply to hidden files.").required(FALSE).build()
        };
    }

    private Option[] buildHelpOptions() {
        return new Option[]{Option.builder(HELP_OPTION).longOpt("help").hasArg(FALSE).desc("Shows the help.")
                .required(FALSE).build()};
    }

    private String getHelp(final Options options) {
        try (StringWriter out = new StringWriter()) {
            try (PrintWriter writer = new PrintWriter(out)) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(writer, WIDTH, CMD_LINE_SYNTAX, HEADER, options, LEFT_PAD, DESC_PAD, FOOTER);
                writer.flush();
            }
            return out.toString();
        } catch (Exception e) {
            log.log(FINE, e.getMessage(), e);
            return e.getMessage();
        }
    }
}
