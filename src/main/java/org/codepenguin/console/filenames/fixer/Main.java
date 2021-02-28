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

import java.util.Collection;
import java.util.Optional;

import static java.util.logging.Level.INFO;

/**
 * Main class.
 *
 * @author Jorge Garcia
 * @version 1.0.0
 * @since 1.8
 */
@Log
public final class Main {

    private static final String NO_CHANGE_REQUEST_MSG = "There isn't a change request built. Please check the " +
            "arguments or execute the application with debug level.";

    private Main() {
    }

    /**
     * Main method.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        final ProcessResponse<ChangeRequest> processResponse = new BuildChangeRequestFunction().apply(args);
        if (!processResponse.isSuccessful()) {
            log.log(INFO, processResponse.getMessage());
            return;
        }

        final Optional<ChangeRequest> changeRequest = processResponse.getResponse();
        if (!changeRequest.isPresent()) {
            log.log(INFO, NO_CHANGE_REQUEST_MSG);
            return;
        }

        final Collection<ChangeResponse> changeResponses = new Fixer().fix(changeRequest.get());
        if (changeResponses.isEmpty()) {
            return;
        }

        changeResponses.forEach(changeResponse -> log.log(INFO, String.valueOf(changeResponse)));
    }
}
