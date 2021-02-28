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
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.util.Arrays;
import java.util.function.Function;
import java.util.logging.Level;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
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

    @Override
    public ProcessResponse<ChangeRequest> apply(final String[] args) {
        if (isEmpty(args)) {
            return new ProcessResponse<>(FALSE, "There isn't arguments.");
        }

        final CommandLineParser parser = new DefaultParser();
        final CommandLine commandLine;
        try {
            commandLine = parser.parse(buildOptions(), args);
        } catch (ParseException e) {
            log.log(Level.FINE, Arrays.toString(args), e);
            return new ProcessResponse<>(FALSE, e.getMessage());
        }

        final File file = new File(commandLine.getOptionValue(FILE_OPTION));
        final ChangeRequestOptions requestOptions = ChangeRequestOptions.builder()
                .applyToHidden(commandLine.hasOption(APPLY_TO_HIDDEN_OPTION)).build();
        final ChangeRequest changeRequest = new ChangeRequest(file, requestOptions);
        return new ProcessResponse<>(TRUE, String.valueOf(changeRequest), changeRequest);
    }

    private Options buildOptions() {
        final Options options = new Options();
        options.addOption(Option.builder(FILE_OPTION).longOpt("file").hasArg(TRUE)
                .desc("The file or directory to change.").required().build());
        options.addOption(Option.builder(APPLY_TO_HIDDEN_OPTION).longOpt("apply-to-hidden").hasArg(FALSE)
                .desc("Indicates if the process must apply to hidden files.").required(FALSE).build());
        return options;
    }
}
