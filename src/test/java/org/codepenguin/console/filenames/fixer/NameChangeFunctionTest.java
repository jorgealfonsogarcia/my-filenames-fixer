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

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.codepenguin.console.filenames.fixer.Separator.DASH;
import static org.codepenguin.console.filenames.fixer.Separator.UNDERSCORE;
import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link NameChangeFunction}.
 *
 * @author Jorge Garcia
 * @version 1.0.0
 * @since 11
 */
public class NameChangeFunctionTest {

    private final NameChangeFunction function = new NameChangeFunction();

    @Test
    public void givenEmptyWhenApplyThenShouldReturnEmpty() {
        assertEquals(EMPTY, function.apply(new NameChangeRequest(EMPTY, DASH)));
    }

    @Test
    public void givenCamelCaseAndUnderscoreWhenApplyThenShouldReturnSnakeCase() {
        assertEquals("my_dir_name", function.apply(new NameChangeRequest("MyDirName", UNDERSCORE)));
    }

    @Test
    public void givenSnakeCaseWhenApplyThenShouldReturnSameName() {
        assertEquals("my_dir_name", function.apply(new NameChangeRequest("my_dir_name", UNDERSCORE)));
    }

    @Test
    public void givenCamelCaseAndDashWhenApplyThenShouldReturnKebabCase() {
        assertEquals("my-file-name", function.apply(new NameChangeRequest("MyFileName", DASH)));
    }

    @Test
    public void givenKebabCaseWhenApplyThenShouldReturnSameName() {
        assertEquals("my-file-name", function.apply(new NameChangeRequest("my-file-name", DASH)));
    }

    @Test
    public void givenNameWithWhitespacesWhenApplyThenShouldTrimAndReplaceThemWithSeparator() {
        assertEquals("my-file-name", function.apply(new NameChangeRequest("My   File Name    ", DASH)));
    }

    @Test
    public void givenUpperCaseWithDashWhenApplyThenShouldReturnKebabCase() {
        assertEquals("my-file-name", function.apply(new NameChangeRequest("MY FILE NAME", DASH)));
    }

    @Test
    public void givenNameWithNumbersWhenApplyThenShouldReturnLettersAndNumbersSeparatedWithSeparator() {
        assertEquals("abc-123", function.apply(new NameChangeRequest("ABC123", DASH)));
    }

    @Test
    public void givenFullUpperCaseWhenApplyThenShouldFullLowerCase() {
        assertEquals("name", function.apply(new NameChangeRequest("NAME", DASH)));
    }
}