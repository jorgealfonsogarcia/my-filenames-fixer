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

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ONE;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_TWO;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ZERO;
import static org.codepenguin.console.filenames.fixer.ChangeErrorType.IO_EXCEPTION_MOVE_DIRECTORY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FileUtils.class, Logger.class})
public class FilenameChangeFunctionTest {

    private static final String FUNCTION_FIELD_NAME = "function";
    private static final String MOVE_DIRECTORY_METHOD_NAME = "moveDirectory";

    @Mock
    private NameChangeFunction nameChangeFunctionMock;

    @Mock
    private Path pathMock;

    @Mock
    private Path newPathMock;

    @Mock
    private File newFileMock;

    @Mock
    private File fileMock;

    private final FilenameChangeFunction function = new FilenameChangeFunction();

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void test1() {
        when(fileMock.isHidden()).thenReturn(TRUE);

        final ChangeRequest.Options options = ChangeRequest.Options.builder().applyToHidden(false).build();
        final ChangeRequest request = ChangeRequest.builder().file(fileMock).options(options).build();

        final Optional<ChangeResponse> response = function.apply(request);

        verify(fileMock, times(INTEGER_ONE)).isHidden();

        assertNotNull(response);
        assertFalse(response.isPresent());
    }

    @Test
    public void test2() {
        when(fileMock.isHidden()).thenReturn(FALSE);
        when(fileMock.getName()).thenReturn("dir_name");
        when(fileMock.isDirectory()).thenReturn(TRUE);

        setInternalState(function, FUNCTION_FIELD_NAME, nameChangeFunctionMock);
        when(nameChangeFunctionMock.apply(any(NameChangeRequest.class))).thenReturn("dir_name");

        final ChangeRequest.Options options = ChangeRequest.Options.builder().applyToHidden(false).build();
        final ChangeRequest request = ChangeRequest.builder().file(fileMock).options(options).build();

        final Optional<ChangeResponse> response = function.apply(request);

        verify(fileMock, times(INTEGER_ONE)).isHidden();
        verify(fileMock, times(INTEGER_ONE)).getName();
        verify(fileMock, times(INTEGER_ONE)).isDirectory();

        verify(nameChangeFunctionMock, times(INTEGER_ONE)).apply(any(NameChangeRequest.class));

        assertNotNull(response);
        assertFalse(response.isPresent());
    }

    @Test
    public void test3() {
        when(fileMock.isHidden()).thenReturn(TRUE);
        when(fileMock.getName()).thenReturn(".file-name");
        when(fileMock.isDirectory()).thenReturn(FALSE);

        setInternalState(function, FUNCTION_FIELD_NAME, nameChangeFunctionMock);
        when(nameChangeFunctionMock.apply(any(NameChangeRequest.class))).thenReturn(".file-name");

        final ChangeRequest.Options options = ChangeRequest.Options.builder().applyToHidden(true).build();
        final ChangeRequest request = ChangeRequest.builder().file(fileMock).options(options).build();

        final Optional<ChangeResponse> response = function.apply(request);

        verify(fileMock, times(INTEGER_ZERO)).isHidden();
        verify(fileMock, times(INTEGER_ONE)).getName();
        verify(fileMock, times(INTEGER_ONE)).isDirectory();

        verify(nameChangeFunctionMock, times(INTEGER_ONE)).apply(any(NameChangeRequest.class));

        assertNotNull(response);
        assertFalse(response.isPresent());
    }

    @SneakyThrows
    @Test
    public void test4() {
        final String oldDirName = "DIR-NAME";
        final String newDirName = "dir-name";

        when(newFileMock.isHidden()).thenReturn(FALSE);
        when(newFileMock.getName()).thenReturn(newDirName);
        when(newFileMock.isDirectory()).thenReturn(TRUE);

        when(newPathMock.toFile()).thenReturn(newFileMock);

        when(pathMock.resolveSibling(anyString())).thenReturn(newPathMock);

        when(fileMock.toPath()).thenReturn(pathMock);
        when(fileMock.isHidden()).thenReturn(FALSE);
        when(fileMock.getName()).thenReturn(oldDirName);
        when(fileMock.isDirectory()).thenReturn(TRUE);

        setInternalState(function, FUNCTION_FIELD_NAME, nameChangeFunctionMock);

        when(nameChangeFunctionMock.apply(any(NameChangeRequest.class))).thenReturn(newDirName);

        mockStatic(FileUtils.class);
        doNothing().when(FileUtils.class, MOVE_DIRECTORY_METHOD_NAME, any(File.class), any(File.class));

        final ChangeRequest.Options options = ChangeRequest.Options.builder().applyToHidden(false).build();
        final ChangeRequest request = ChangeRequest.builder().file(fileMock).options(options).build();

        final Optional<ChangeResponse> response = function.apply(request);

        verify(newFileMock, times(INTEGER_ZERO)).isHidden();
        verify(newFileMock, times(INTEGER_ZERO)).getName();
        verify(newFileMock, times(INTEGER_ZERO)).isDirectory();

        verify(newPathMock, times(INTEGER_TWO)).toFile();

        verify(pathMock, times(INTEGER_TWO)).resolveSibling(anyString());

        verify(fileMock, times(INTEGER_TWO)).toPath();
        verify(fileMock, times(INTEGER_ONE)).isHidden();
        verify(fileMock, times(INTEGER_ONE)).getName();
        verify(fileMock, times(INTEGER_TWO)).isDirectory();

        verify(nameChangeFunctionMock, times(INTEGER_ONE)).apply(any(NameChangeRequest.class));

        verifyStatic(FileUtils.class, times(INTEGER_TWO));

        assertNotNull(response);
        assertTrue(response.isPresent());

        final ChangeResponse changeResponse = response.get();
        assertEquals(request, changeResponse.getRequest());
        assertNull(changeResponse.getChangeErrorType());
        assertNull(changeResponse.getMessage());

        final File newFile = changeResponse.getNewFile();
        assertNotNull(newFile);
        assertFalse(newFile.isHidden());
        assertEquals(newDirName, newFile.getName());
        assertTrue(newFile.isDirectory());
    }

    @SneakyThrows
    @Test
    public void test5() {
        final String oldDirName = "DIR NAME";
        final String newDirName = "dir-name";

        when(newFileMock.isHidden()).thenReturn(FALSE);
        when(newFileMock.getName()).thenReturn(newDirName);
        when(newFileMock.isDirectory()).thenReturn(TRUE);

        when(newPathMock.toFile()).thenReturn(newFileMock);

        when(pathMock.resolveSibling(anyString())).thenReturn(newPathMock);

        when(fileMock.toPath()).thenReturn(pathMock);
        when(fileMock.isHidden()).thenReturn(FALSE);
        when(fileMock.getName()).thenReturn(oldDirName);
        when(fileMock.isDirectory()).thenReturn(TRUE);

        setInternalState(function, FUNCTION_FIELD_NAME, nameChangeFunctionMock);

        when(nameChangeFunctionMock.apply(any(NameChangeRequest.class))).thenReturn(newDirName);

        mockStatic(FileUtils.class);
        doNothing().when(FileUtils.class, MOVE_DIRECTORY_METHOD_NAME, any(File.class), any(File.class));

        final ChangeRequest.Options options = ChangeRequest.Options.builder().applyToHidden(false).build();
        final ChangeRequest request = ChangeRequest.builder().file(fileMock).options(options).build();

        final Optional<ChangeResponse> response = function.apply(request);

        verify(newFileMock, times(INTEGER_ZERO)).isHidden();
        verify(newFileMock, times(INTEGER_ZERO)).getName();
        verify(newFileMock, times(INTEGER_ZERO)).isDirectory();

        verify(newPathMock, times(INTEGER_ONE)).toFile();

        verify(pathMock, times(INTEGER_ONE)).resolveSibling(anyString());

        verify(fileMock, times(INTEGER_ONE)).toPath();
        verify(fileMock, times(INTEGER_ONE)).isHidden();
        verify(fileMock, times(INTEGER_ONE)).getName();
        verify(fileMock, times(INTEGER_TWO)).isDirectory();

        verify(nameChangeFunctionMock, times(INTEGER_ONE)).apply(any(NameChangeRequest.class));

        verifyStatic(FileUtils.class, times(INTEGER_TWO));

        assertNotNull(response);
        assertTrue(response.isPresent());

        final ChangeResponse changeResponse = response.get();
        assertEquals(request, changeResponse.getRequest());
        assertNull(changeResponse.getChangeErrorType());
        assertNull(changeResponse.getMessage());

        final File newFile = changeResponse.getNewFile();
        assertNotNull(newFile);
        assertFalse(newFile.isHidden());
        assertEquals(newDirName, newFile.getName());
        assertTrue(newFile.isDirectory());
    }

    @SneakyThrows
    @Test
    public void test6() {
        final String oldDirName = "DIR NAME";
        final String newDirName = "dir-name";

        when(newFileMock.isHidden()).thenReturn(FALSE);
        when(newFileMock.getName()).thenReturn(newDirName);
        when(newFileMock.isDirectory()).thenReturn(TRUE);

        when(newPathMock.toFile()).thenReturn(newFileMock);

        when(pathMock.resolveSibling(anyString())).thenReturn(newPathMock);

        when(fileMock.toPath()).thenReturn(pathMock);
        when(fileMock.isHidden()).thenReturn(FALSE);
        when(fileMock.getName()).thenReturn(oldDirName);
        when(fileMock.isDirectory()).thenReturn(TRUE);

        setInternalState(function, FUNCTION_FIELD_NAME, nameChangeFunctionMock);

        when(nameChangeFunctionMock.apply(any(NameChangeRequest.class))).thenReturn(newDirName);

        mockStatic(FileUtils.class);

        final String exceptionMessage = "IOException message";
        doThrow(new IOException(exceptionMessage))
                .when(FileUtils.class, MOVE_DIRECTORY_METHOD_NAME, any(File.class), any(File.class));

        final ChangeRequest.Options options = ChangeRequest.Options.builder().applyToHidden(false).build();
        final ChangeRequest request = ChangeRequest.builder().file(fileMock).options(options).build();

        final Optional<ChangeResponse> response = function.apply(request);

        verify(newFileMock, times(INTEGER_ZERO)).isHidden();
        verify(newFileMock, times(INTEGER_ZERO)).getName();
        verify(newFileMock, times(INTEGER_ZERO)).isDirectory();

        verify(newPathMock, times(INTEGER_ONE)).toFile();

        verify(pathMock, times(INTEGER_ONE)).resolveSibling(anyString());

        verify(fileMock, times(INTEGER_ONE)).toPath();
        verify(fileMock, times(INTEGER_ONE)).isHidden();
        verify(fileMock, times(INTEGER_ONE)).getName();
        verify(fileMock, times(INTEGER_TWO)).isDirectory();

        verify(nameChangeFunctionMock, times(INTEGER_ONE)).apply(any(NameChangeRequest.class));

        verifyStatic(FileUtils.class, times(INTEGER_TWO));

        assertNotNull(response);
        assertTrue(response.isPresent());

        final ChangeResponse changeResponse = response.get();
        assertEquals(request, changeResponse.getRequest());
        assertEquals(IO_EXCEPTION_MOVE_DIRECTORY, changeResponse.getChangeErrorType());
        assertEquals(exceptionMessage, changeResponse.getMessage());

        assertNull(changeResponse.getNewFile());
    }

}