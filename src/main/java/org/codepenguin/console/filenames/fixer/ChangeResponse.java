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

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.File;
import java.util.Objects;

import static lombok.AccessLevel.PRIVATE;

/**
 * Response from a change filename request.
 *
 * @author Jorge Garcia
 * @version 1.0.0
 * @since 1.8
 */
@Getter
@AllArgsConstructor(access = PRIVATE)
@EqualsAndHashCode
@ToString
class ChangeResponse {

    private final ChangeRequest request;
    private final File newFile;
    private final ChangeErrorType changeErrorType;
    private final String message;

    /**
     * Constructor for a success process.
     *
     * @param request The original request.
     * @param newFile The new file generated.
     */
    ChangeResponse(ChangeRequest request, File newFile) {
        this(request, newFile, null, null);
    }

    /**
     * Constructor for a error process.
     *
     * @param request         The original request.
     * @param changeErrorType The change error type.
     * @param message         The error message.
     */
    ChangeResponse(ChangeRequest request, ChangeErrorType changeErrorType, String message) {
        this(request, null, changeErrorType, message);
    }

    /**
     * Indicates if the filename change process was successfully applied.
     *
     * @return {@code true} if the process was successfully; otherwise, {@code false}.
     */
    boolean wasSuccessfullyApplied() {
        return Objects.nonNull(newFile);
    }
}
