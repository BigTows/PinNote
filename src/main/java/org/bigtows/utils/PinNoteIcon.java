package org.bigtows.utils;

import com.intellij.icons.AllIcons;
import com.intellij.util.SVGLoader;
import com.intellij.util.ui.JBImageIcon;

import javax.swing.*;
import java.util.Objects;
import java.util.Optional;

public class PinNoteIcon {

    public static final Icon NOTIFICATION_PIN_NOTE = PinNoteIcon.getSvgIconFromResource(
            "/icons/toolWindowRight_dark.svg", 1.2f
    ).orElse(AllIcons.Ide.FatalError);

    public static final Icon TAB_EVERNOTE_ICON = PinNoteIcon.getSvgIconFromResource(
            "/icons/evernote.svg", 0.5f
    ).orElse(AllIcons.Ide.FatalError);

    public static final Icon SETTINGS_EVERNOTE_ICON = PinNoteIcon.getSvgIconFromResource(
            "/icons/evernote.svg", 1.2f
    ).orElse(AllIcons.Ide.FatalError);

    public static Optional<Icon> getSvgIconFromResource(String resource, float scale) {
        try {
            return Optional.of(
                    new JBImageIcon(
                            SVGLoader.load(
                                    Objects.requireNonNull(PinNoteIcon.class.getClassLoader().getResource(resource)),
                                    scale
                            )
                    )
            );
        } catch (Throwable e) {
            return Optional.empty();
        }
    }
}
