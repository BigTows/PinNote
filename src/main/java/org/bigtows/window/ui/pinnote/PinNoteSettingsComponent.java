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
        contentPanel2 = new JPanel();
        contentPanel = new JBScrollPane(
                contentPanel2,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        contentPanel.setBorder(BorderFactory.createEmptyBorder());
        var layout = new GridLayout(0, 1);
        contentPanel2.setLayout(layout);

    }
}


