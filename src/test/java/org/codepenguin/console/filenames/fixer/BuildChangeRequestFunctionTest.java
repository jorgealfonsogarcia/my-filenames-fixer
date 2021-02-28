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

import static org.apache.commons.lang3.ArrayUtils.EMPTY_STRING_ARRAY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.split;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BuildChangeRequestFunctionTest {

    private final BuildChangeRequestFunction function = new BuildChangeRequestFunction();

    @Test
    public void apply1() {
        final ProcessResponse<ChangeRequest> response = function.apply(null);
        assertNotNull(response);
        assertFalse(response.isSuccessful());
        assertTrue(isNotBlank(response.getMessage()));
        assertFalse(response.getResponse().isPresent());
    }

    @Test
    public void apply2() {
        final ProcessResponse<ChangeRequest> response = function.apply(EMPTY_STRING_ARRAY);
        assertNotNull(response);
        assertFalse(response.isSuccessful());
        assertTrue(isNotBlank(response.getMessage()));
        assertFalse(response.getResponse().isPresent());
    }

    @Test
    public void apply3() {
        final ProcessResponse<ChangeRequest> response = function.apply(split("x"));
        assertNotNull(response);
        assertFalse(response.isSuccessful());
        assertTrue(isNotBlank(response.getMessage()));
        assertFalse(response.getResponse().isPresent());
    }

    @Test
    public void apply4() {
        final ProcessResponse<ChangeRequest> response = function.apply(split("f"));
        assertNotNull(response);
        assertFalse(response.isSuccessful());
        assertTrue(isNotBlank(response.getMessage()));
        assertFalse(response.getResponse().isPresent());
    }
}