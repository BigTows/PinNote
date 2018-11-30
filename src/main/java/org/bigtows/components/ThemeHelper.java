/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.components;

import com.intellij.openapi.wm.impl.ToolWindowImpl;
import org.bigtows.components.enums.ThemeEnum;
import org.bigtows.components.exception.PinNoteComponentException;

/**
 *
 */
public class ThemeHelper {

    /**
     * Get theme by Tool window
     *
     * @param toolWindow toolWindow instance
     * @return theme enum
     */
    public ThemeEnum getTheme(ToolWindowImpl toolWindow) {

        if (toolWindow.getContentUI().getBackground().getRGB() == -12828863) {
            return ThemeEnum.DRACULA;
        }
        return ThemeEnum.LIGHT;
    }

    /**
     * Get name resource by theme enum
     *
     * @param themeEnum enum
     * @return name resource
     */
    public String getNameResource(ThemeEnum themeEnum) {
        switch (themeEnum) {
            case DRACULA:
                return "NoteViewDracula.css";
            case LIGHT:
                return "NoteViewLight.css";
            default:
                throw new PinNoteComponentException("Try get resource for undefined theme: " + themeEnum.toString());
        }
    }

}
