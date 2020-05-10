package org.bigtows.notebook.local;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LocalNotebookStorage {

    private List<LocalNote> noteList = new ArrayList<>();
}
