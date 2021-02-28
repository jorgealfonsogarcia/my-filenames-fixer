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

import java.util.Arrays;

import static org.apache.commons.lang3.ArrayUtils.EMPTY_STRING_ARRAY;
import static org.apache.commons.lang3.StringUtils.split;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BuildChangeRequestFunctionTest {

    private static final String THERE_ISN_T_ARGUMENTS_MSG = "There isn't arguments.";
    private static final String UNRECOGNIZED_OPTION_X_MSG = "Unrecognized option: -x";

    private final BuildChangeRequestFunction function = new BuildChangeRequestFunction();

    @Test
    public void apply1() {
        final ProcessResponse<ChangeRequest> response = function.apply(null);
        assertNotNull(response);
        assertFalse(response.isSuccessful());
        assertEquals(THERE_ISN_T_ARGUMENTS_MSG, response.getMessage());
        assertFalse(response.getResponse().isPresent());
    }

    @Test
    public void apply2() {
        final ProcessResponse<ChangeRequest> response = function.apply(EMPTY_STRING_ARRAY);
        assertNotNull(response);
        assertFalse(response.isSuccessful());
        assertEquals(THERE_ISN_T_ARGUMENTS_MSG, response.getMessage());
        assertFalse(response.getResponse().isPresent());
    }

    @Test
    public void apply7() {
        final ProcessResponse<ChangeRequest> response = function.apply(split("-h"));
        assertNotNull(response);
        assertTrue(response.isSuccessful());
        final String[] expectedTexts = {
                "usage: java -jar my-filenames-fixer.jar",
                "My Filenames Fixer",
                "     -a,--apply-to-hidden     Indicates if the process must apply to hidden files.",
                "     -f,--file                The file or directory to change.",
                "     -h,--help                Shows the help.",
                "v1.0.0"
        };
        assertTrue(Arrays.stream(expectedTexts).allMatch(expectedText -> response.getMessage().contains(expectedText)));
        assertFalse(response.getResponse().isPresent());
    }

    @Test
    public void apply8() {
        final ProcessResponse<ChangeRequest> response = function.apply(split("-h -x"));
        assertNotNull(response);
        assertFalse(response.isSuccessful());
        assertEquals(UNRECOGNIZED_OPTION_X_MSG, response.getMessage());
        assertFalse(response.getResponse().isPresent());
    }

    @Test
    public void apply3() {
        final ProcessResponse<ChangeRequest> response = function.apply(split("-x"));
        assertNotNull(response);
        assertFalse(response.isSuccessful());
        assertEquals(UNRECOGNIZED_OPTION_X_MSG, response.getMessage());
        assertFalse(response.getResponse().isPresent());
    }

    @Test
    public void apply4() {
        final ProcessResponse<ChangeRequest> response = function.apply(split("-f"));
        assertNotNull(response);
        assertFalse(response.isSuccessful());
        assertEquals("Missing argument for option: f", response.getMessage());
        assertFalse(response.getResponse().isPresent());
    }

    @Test
    public void apply5() {
        final String filename = "myDir";
        final ProcessResponse<ChangeRequest> response = function.apply(split(String.format("-f /tmp/%s", filename)));
        assertNotNull(response);
        assertTrue(response.isSuccessful());
        assertEquals("ChangeRequest(file=\\tmp\\myDir, options=ChangeRequestOptions(applyToHidden=false))",
                response.getMessage());
        assertTrue(response.getResponse().isPresent());

        final ChangeRequest changeRequest = response.getResponse().get();
        assertNotNull(changeRequest.getFile());
        assertEquals(filename, changeRequest.getFile().getName());
        assertFalse(changeRequest.getOptions().isApplyToHidden());
    }

    @Test
    public void apply6() {
        final String filename = "myDir";
        final ProcessResponse<ChangeRequest> response = function.apply(split(String.format("-f /tmp/%s -a", filename)));
        assertNotNull(response);
        assertTrue(response.isSuccessful());
        assertEquals("ChangeRequest(file=\\tmp\\myDir, options=ChangeRequestOptions(applyToHidden=true))",
                response.getMessage());
        assertTrue(response.getResponse().isPresent());

        final ChangeRequest changeRequest = response.getResponse().get();
        assertNotNull(changeRequest.getFile());
        assertEquals(filename, changeRequest.getFile().getName());
        assertTrue(changeRequest.getOptions().isApplyToHidden());
    }
}