package org.bigtows.window.ui.pinnote.settings;

import lombok.Builder;
import lombok.Data;

import javax.swing.*;

/**
 * Information about source notebook
 */
@Data
@Builder
public class NotebookSource {

    /**
     * Name of source
     */
    private String name;

    /**
     * Icon for display in settings
     */
    private Icon icon;

    /**
     * Current status of source
     */
    private StatusSource status;

    /**
     * Information about source (Support HTML)
     */
    private String description;

    /**
     * Handler of action when user want enable, disable or fix source.
     */
    private SourceAction action;
}
