package org.bigtows.window.ui.pinnote;

import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBImageIcon;
import org.bigtows.window.ui.pinnote.settings.NotebookSource;

import javax.swing.*;
import java.awt.*;

public class OAuthPanel extends JPanel {

    private final JLabel iconSource = new JBLabel();
    private final JLabel nameSource = new JBLabel();
    private final JLabel descriptionSource = new JBLabel();


    public OAuthPanel(NotebookSource source) {

        setMinimumSize(new Dimension(100, 500));
        setPreferredSize(new Dimension(100, 500));
        setMaximumSize(new Dimension(100, 500));
        setLayout(new GridBagLayout());


        var nameSourceContains = new GridBagConstraints();
        nameSourceContains.gridx = 0;
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
        descriptionSourceContains.weightx = 1.0f;
        descriptionSourceContains.weighty = 0.1f;

        descriptionSource.setText(source.getDescription());
        descriptionSource.setMinimumSize(new Dimension(300, 500));
        descriptionSource.setPreferredSize(new Dimension(300, 500));
        descriptionSource.setMaximumSize(new Dimension(300, 500));

        add(descriptionSource, descriptionSourceContains);


        var iconSourceContains = new GridBagConstraints();
        iconSourceContains.gridx = 0;
        iconSourceContains.gridy = 1;
        iconSourceContains.fill = GridBagConstraints.HORIZONTAL;
        iconSourceContains.weightx = 0.1f;
        iconSourceContains.weighty = 0.1f;

        iconSource.setOpaque(false);
        iconSource.setIcon(new JBImageIcon(source.getImage()));

        add(iconSource, iconSourceContains);


        var statusContains = new GridBagConstraints();
        statusContains.gridx = 2;
        statusContains.gridy = 0;
        statusContains.fill = GridBagConstraints.HORIZONTAL;
        statusContains.weightx = 0.1f;
        statusContains.weighty = 0.1f;

        var button = this.initButton(source);

        add(button, statusContains);

    }

    private JButton initButton(NotebookSource source) {
        var button = new JButton();
        if (source.isStatus()) {
            button.setText("<html><font color='red'>Deactivate</font></html>");
        } else {
            button.setText("Activate");
        }
        return button;
    }
}
