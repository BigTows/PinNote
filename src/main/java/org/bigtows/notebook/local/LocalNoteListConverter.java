package org.bigtows.notebook.local;

import com.google.gson.Gson;
import com.intellij.util.xmlb.Converter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LocalNoteListConverter extends Converter<LocalNotebookStorage> {

    private final Gson jsonParser;

    public LocalNoteListConverter() {
        this.jsonParser = new Gson();
    }

    @Nullable
    @Override
    public LocalNotebookStorage fromString(@NotNull String value) {
        return jsonParser.fromJson(value, LocalNotebookStorage.class);
    }

    @Override
    public @Nullable String toString(@NotNull LocalNotebookStorage value) {
        return jsonParser.toJson(value);
    }
}
