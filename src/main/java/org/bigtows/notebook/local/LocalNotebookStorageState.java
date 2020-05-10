package org.bigtows.notebook.local;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.OptionTag;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "LocalNotebook", storages = @Storage("localNotebook.xml"))
@Data
public class LocalNotebookStorageState implements PersistentStateComponent<LocalNotebookStorageState> {

    @OptionTag(converter = LocalNoteListConverter.class)
    private LocalNotebookStorage storage = new LocalNotebookStorage();

    @Nullable
    @Override
    public LocalNotebookStorageState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull LocalNotebookStorageState state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
