package org.bigtows.service;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import org.bigtows.service.state.EvernoteState;
import org.bigtows.service.state.LocalStorageState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
@State(name = "PinNote", storages = @Storage("pinNote.xml"))
public class PinNoteState implements PersistentStateComponent<PinNoteState> {

    private EvernoteState evernoteState = new EvernoteState();

    private LocalStorageState localStorageState = new LocalStorageState();

    @Nullable
    @Override
    public PinNoteState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull PinNoteState state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
