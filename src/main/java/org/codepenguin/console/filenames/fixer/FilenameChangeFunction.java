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

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

import static java.util.UUID.randomUUID;
import static java.util.logging.Level.SEVERE;
import static org.apache.commons.io.FileUtils.moveDirectory;
import static org.apache.commons.io.FileUtils.moveFile;
import static org.codepenguin.console.filenames.fixer.ChangeError.IO_EXCEPTION_MOVE_DIRECTORY;
import static org.codepenguin.console.filenames.fixer.ChangeError.IO_EXCEPTION_MOVE_FILE;
import static org.codepenguin.console.filenames.fixer.Separator.DASH;
import static org.codepenguin.console.filenames.fixer.Separator.UNDERSCORE;


/**
 * Function that applies the change of the filename from the requested options.
 *
 * @author Jorge Garcia
 * @version 1.0.0
 * @since 11
 */
@Log
class FilenameChangeFunction implements Function<ChangeRequest, Optional<ChangeResult>> {

    private final NameChangeFunction function = new NameChangeFunction();

    @Override
    public Optional<ChangeResult> apply(final ChangeRequest request) {
        final var file = request.getFile();
        if (!request.getOptions().isApplyToHidden() && file.isHidden()) {
            return Optional.empty();
        }

        final var newFilename = function.apply(new NameChangeRequest(request.getFile().getName(),
                request.getFile().isDirectory() ? UNDERSCORE : DASH));
        if (newFilename.equals(request.getFile().getName())) {
            return Optional.empty();
        }

        final var mustUseTmpFile = newFilename.equalsIgnoreCase(request.getFile().getName());

        final var newFile = request.getFile().toPath().resolveSibling(newFilename).toFile();
        if (request.getFile().isDirectory()) {
            try {
                if (mustUseTmpFile) {
                    final var tmpFile = buildTmpFile(request.getFile());
                    moveDirectory(request.getFile(), tmpFile);
                    moveDirectory(tmpFile, newFile);
                } else {
                    moveDirectory(request.getFile(), newFile);
                }
            } catch (IOException e) {
                log.log(SEVERE, String.valueOf(request), e);
                return Optional.of(new ChangeResult(request, IO_EXCEPTION_MOVE_DIRECTORY));
            }

            return Optional.of(new ChangeResult(request, newFile));
        }

        try {
            if (mustUseTmpFile) {
                final var tmpFile = buildTmpFile(request.getFile());
                moveFile(request.getFile(), tmpFile);
                moveFile(tmpFile, newFile);
            } else {
                moveFile(request.getFile(), newFile);
            }
        } catch (IOException e) {
            log.log(SEVERE, String.valueOf(request), e);
            return Optional.of(new ChangeResult(request, IO_EXCEPTION_MOVE_FILE));
        }

        return Optional.of(new ChangeResult(request, newFile));
    }

    private File buildTmpFile(final File file) {
        return file.toPath().resolveSibling(randomUUID().toString()).toFile();
    }
}
