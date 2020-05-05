package org.bigtows.window.ui.pinnote.settings;

import lombok.Builder;
import lombok.Data;

import java.awt.*;

@Data
@Builder
public class NotebookSource {

    private String name;

    private Image image;

    private boolean status;

    private String description;
}
