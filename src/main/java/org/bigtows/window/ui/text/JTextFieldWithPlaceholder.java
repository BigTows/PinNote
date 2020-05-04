package org.bigtows.window.ui.text;

import com.intellij.ui.components.JBTextField;

import java.awt.*;

/**
 * @link https://stackoverflow.com/questions/16213836/java-swing-jtextfield-set-placeholder
 */
public class JTextFieldWithPlaceholder extends JBTextField {

    private final String placeholder;

    public JTextFieldWithPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public JTextFieldWithPlaceholder(String text, String placeholder) {
        super(text);
        this.placeholder = placeholder;
    }


    @Override
    protected void paintComponent(final Graphics pG) {
        super.paintComponent(pG);

        if (placeholder == null || placeholder.length() == 0 || getText().length() > 0) {
            return;
        }

        final Graphics2D g = (Graphics2D) pG;
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(getDisabledTextColor());
        g.drawString(placeholder, getInsets().left + 3, pG.getFontMetrics()
                .getMaxAscent() + getInsets().top - 1);
    }
}
