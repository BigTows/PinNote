/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.window;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class RigthToolWindowTest {

    @Mock
    private Project project;

    @Mock
    private ToolWindow toolWindow;

    @Mock
    private PropertiesComponent component;

    private RightToolWindowFactory rightToolWindowFactory;

    @Before
    public void setup() {
       // this.rightToolWindow = new RightToolWindow();
    }


    @Test
    public void test() {

    //    rightToolWindow.createToolWindowContent(project,toolWindow);
    }
}
