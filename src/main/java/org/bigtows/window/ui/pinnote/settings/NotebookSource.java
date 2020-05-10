package org.bigtows.window.ui.pinnote.settings;

import lombok.Builder;
import lombok.Data;

import javax.swing.*;
import java.awt.*;

@Data
@Builder
public class NotebookSource {

    private String name;

    private Icon icon;

    private StatusSource status;

    private String description;

    private SourceAction action;
}
