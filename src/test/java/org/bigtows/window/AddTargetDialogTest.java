/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.window;

import org.bigtows.window.dialog.AddTargetDialog;
import org.junit.Test;

import javax.swing.*;

public class AddTargetDialogTest {

    @Test
    public void a(){
        SwingUtilities.invokeLater(()->{
            AddTargetDialog dialog = new AddTargetDialog(true);
            dialog.getNameTarget();
        });

    }
}
