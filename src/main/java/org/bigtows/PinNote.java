package org.bigtows;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;


public class PinNote implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
        System.out.println("TEST");
    }
}
