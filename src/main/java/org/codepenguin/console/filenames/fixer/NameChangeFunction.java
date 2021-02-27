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

import java.util.function.Function;

import static java.lang.String.*;
import static java.util.Locale.ROOT;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * Function that applies the name change.
 *
 * @author Jorge Garcia
 * @version 1.0.0
 * @since 11
 */
class NameChangeFunction implements Function<NameChangeRequest, String> {

    private static final String ALL_WHITESPACES_REGEX = "\\s+";
    private static final String REPEATED_SEPARATOR_REGEX_FORMAT = "[%s]+";

    @Override
    public String apply(final NameChangeRequest request) {
        final String separator = valueOf(request.getSeparator().getCharacter());
        return join(SPACE, splitByCharacterTypeCamelCase(trim(request.getName()))).toLowerCase(ROOT)
                .replaceAll(ALL_WHITESPACES_REGEX, separator)
                .replaceAll(format(REPEATED_SEPARATOR_REGEX_FORMAT, separator), separator);
    }

}
