/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.intellij.openapi.components.ApplicationComponent;
import org.bigtows.config.PinNoteConfigurator;
import org.jetbrains.annotations.NotNull;

public class PinNote implements ApplicationComponent {


    public static Injector injector;

    @NotNull
    @Override
    public String getComponentName() {
        return "PinNote";
    }

    @Override
    public void initComponent() {
        injector = Guice.createInjector(new PinNoteConfigurator());
    }

}
