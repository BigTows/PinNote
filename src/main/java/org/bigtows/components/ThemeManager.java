/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.components;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.wm.impl.ToolWindowImpl;
import org.bigtows.components.enums.ThemeEnum;

public class ThemeManager implements ApplicationComponent {

    public ThemeManager() {
    }


    public ThemeEnum getTheme(ToolWindowImpl toolWindow) {

        if (toolWindow.getContentUI().getBackground().getRGB() == -12828863) {
            return ThemeEnum.DARCULA;
        }
        return ThemeEnum.LIGHT;
        // project.getCo
    }


    public String getNameResource(ThemeEnum themeEnum) {
        switch (themeEnum) {
            case DARCULA:
                return "NoteViewDracula.css";
            case LIGHT:
                return "NoteViewLight.css";
        }
        return "WHAT??!??!?!";
    }

}
