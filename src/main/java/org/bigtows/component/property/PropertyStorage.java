package org.bigtows.component.property;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.components.Service;

@Service
public class PropertyStorage {

    /**
     * Instance of JetBrains component for access to property storage.
     */
    private final PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();


    public String getString(String path) {
        return propertiesComponent.getValue(path);
    }

    public void setString(String path, String value) {
        propertiesComponent.setValue(path, value);
    }
}
