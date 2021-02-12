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

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.codepenguin.console.filenames.fixer.FileType.DIR;
import static org.codepenguin.console.filenames.fixer.FileType.FILE;

/**
 * Fixes the filenames.
 *
 * @author Jorge Garcia
 * @version 1.0.0
 * @since 11
 */
final class Fixer {

    private static final String WHITESPACE_REGEX = "\\s+";
    private static final String UNDERSCORE_SEPARATOR = "_";
    private static final String DASH_SEPARATOR = "-";

    /**
     * Fixes the filename of the requested file and the inner files.
     *
     * @param request The change request.
     * @return A collection with the change results.
     */
    Collection<ChangeResult> fix(final ChangeRequest request) {
        return fix(request, new ArrayList<>());
    }

    private Collection<ChangeResult> fix(final ChangeRequest request, final List<ChangeResult> results) {
        final var file = request.getFile();
        final var fileType = file.isDirectory() ? DIR : FILE;
        final var parentFile = file.getParentFile();
        final var currentFileName = file.getName();
        final String currentCanonicalPath;
        try {
            currentCanonicalPath = file.getCanonicalPath();
        } catch (IOException e) {
            results.add(ChangeResult.builder().changed(false).fileType(fileType).oldCanonicalFilename(currentFileName)
                    .build());
            return results;
        }

        if (!request.getOptions().isApplyToHidden() && file.isHidden()) {
            results.add(ChangeResult.builder().changed(false).fileType(fileType)
                    .oldCanonicalFilename(currentCanonicalPath).build());
            return results;
        }

        final var newName = currentFileName.toLowerCase(Locale.getDefault())
                .replaceAll(WHITESPACE_REGEX, fileType.equals(DIR) ? UNDERSCORE_SEPARATOR : DASH_SEPARATOR);

        final String newCanonicalFilename;
        try {
            newCanonicalFilename = parentFile.getCanonicalPath() + File.separator + newName;
        } catch (IOException e) {
            results.add(ChangeResult.builder().changed(false).fileType(fileType)
                    .oldCanonicalFilename(currentCanonicalPath).build());
            return results;
        }

        final var newFile = new File(newCanonicalFilename);
        try {
            if (fileType.equals(DIR)) {
                FileUtils.moveDirectory(file, newFile);
            } else {
                FileUtils.moveFile(file, newFile);
            }
            results.add(ChangeResult.builder().changed(true).fileType(fileType)
                    .oldCanonicalFilename(currentCanonicalPath).newCanonicalFilename(newName).build());
        } catch (IOException e) {
            results.add(ChangeResult.builder().changed(false).fileType(fileType)
                    .oldCanonicalFilename(currentCanonicalPath).newCanonicalFilename(newName).build());
        }

        if (fileType.equals(DIR)) {
            final var files = newFile.listFiles();
            if (Objects.nonNull(files)) {
                Arrays.stream(files).forEach(innerFile -> fix(ChangeRequest.builder().file(innerFile)
                        .options(request.getOptions()).build(), results));
            }
        }

        return results;
    }
}
