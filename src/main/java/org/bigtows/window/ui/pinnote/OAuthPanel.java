package org.bigtows.window.ui.pinnote;

import com.intellij.icons.AllIcons;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import org.bigtows.window.ui.border.BottomBorder;
import org.bigtows.window.ui.pinnote.settings.NotebookSource;
import org.bigtows.window.ui.pinnote.settings.StatusSource;

import javax.swing.*;
import java.awt.*;

public class OAuthPanel extends JPanel {

    private final JLabel iconSource = new JBLabel();
    private final JLabel nameSource = new JBLabel();
    private final JLabel descriptionSource = new JBLabel();
    private final JButton button;

    private StatusSource statusSource;


    public OAuthPanel(NotebookSource source) {
        statusSource = source.getStatus();
        setMaximumSize(new Dimension(200, 100));
        setPreferredSize(new Dimension(200, 100));
        setBorder(new BottomBorder(JBUI.CurrentTheme.ToolWindow.borderColor()));
        setLayout(new GridBagLayout());


        var nameSourceContains = new GridBagConstraints();
        nameSourceContains.gridx = 1;
        nameSourceContains.gridy = 0;
        nameSourceContains.fill = GridBagConstraints.HORIZONTAL;
        nameSourceContains.weightx = 0.1f;
        nameSourceContains.weighty = 0.1f;
        nameSource.setText(source.getName());
        add(nameSource, nameSourceContains);


        var descriptionSourceContains = new GridBagConstraints();
        descriptionSourceContains.gridx = 1;
        descriptionSourceContains.gridy = 1;
        descriptionSourceContains.fill = GridBagConstraints.HORIZONTAL;
        descriptionSourceContains.weightx = 0.8f;
        descriptionSourceContains.weighty = 1f;

        descriptionSource.setText(source.getDescription());

        add(descriptionSource, descriptionSourceContains);


        var iconSourceContains = new GridBagConstraints();
        iconSourceContains.gridx = 0;
        iconSourceContains.gridy = 0;
        iconSourceContains.fill = GridBagConstraints.HORIZONTAL;
        iconSourceContains.weightx = 0.4f;
        iconSourceContains.weighty = 0.1f;

        iconSource.setOpaque(false);
        iconSource.setIcon(source.getIcon());

        add(iconSource, iconSourceContains);


        var statusContains = new GridBagConstraints();
        statusContains.gridx = 2;
        statusContains.gridy = 0;
        statusContains.fill = GridBagConstraints.HORIZONTAL;
        statusContains.weightx = 0.1f;
        statusContains.weighty = 0.1f;

        this.button = this.initButton(source);

        add(button, statusContains);

    }

    private JButton initButton(NotebookSource source) {
        var button = new JButton();

        this.fillButtonStatus(button, source.getStatus());

        button.addActionListener(e -> {
            if (source.getAction() != null) {
                source.getAction().call(this, this.statusSource);
            }
        });
        return button;
    }


    public void updateSourceStatus(StatusSource statusSource) {
        fillButtonStatus(this.button, statusSource);
        this.statusSource = statusSource;
    }

    private void fillButtonStatus(JButton button, StatusSource statusSource) {
        button.setIcon(null);
        if (statusSource == StatusSource.ENABLED) {
            button.setText("<html><font color='red'>Deactivate</font></html>");
        } else if (statusSource == StatusSource.HAS_PROBLEM) {
            button.setText("Fix");
            button.setIcon(AllIcons.Actions.QuickfixBulb);
        } else {
            button.setText("Activate");
        }
    }
}
