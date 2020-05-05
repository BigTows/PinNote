package org.bigtows.window.ui.pinnote;

import com.intellij.ui.components.JBScrollPane;
import org.bigtows.window.ui.pinnote.settings.NotebookSource;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PinNoteSettingsComponent {
    private JPanel root;
    private JPanel descriptionPanel;
    private JLabel descriptionLabel;
    private JScrollPane contentPanel;
    private JPanel contentPanel2;


    public JPanel getRoot() {
        return root;
    }

    public void addedSources(List<NotebookSource> sourceList) {
        sourceList.forEach(source -> {
            contentPanel2.add(new OAuthPanel(source));
        });
    }

    private void createUIComponents() {
        contentPanel = new JBScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

    }
}


