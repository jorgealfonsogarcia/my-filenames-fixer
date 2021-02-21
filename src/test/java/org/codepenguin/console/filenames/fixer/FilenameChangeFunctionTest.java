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

import org.junit.Test;

import java.io.File;
import java.util.Optional;

import static org.junit.Assert.*;

public class FilenameChangeFunctionTest {

    private final FilenameChangeFunction function = new FilenameChangeFunction();

    @Test
    public void name() {
        File file = new File("C:\\tmp\\my_directory_abc_XXXXXX");
        final var options = ChangeRequest.Options.builder().applyToHidden(false).build();
        final var result = function.apply(new ChangeRequest(file, options));
        assertNotNull(result);
    }
}