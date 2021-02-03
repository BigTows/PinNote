package org.bigtows.utils;

import com.intellij.icons.AllIcons;
import com.intellij.util.SVGLoader;
import com.intellij.util.ui.JBImageIcon;

import javax.swing.*;
import java.util.Objects;
import java.util.Optional;

/**
 * Class with default icons for PinNote plugin
 */
public class PinNoteIcon {

    /**
     * Icon used in notification's
     */
    public static final Icon NOTIFICATION_PIN_NOTE = PinNoteIcon.getSvgIconFromResource(
            "icons/toolWindowRight_dark.svg", 1.2f
    ).orElse(AllIcons.Ide.FatalError);

    /**
     * Icon for evernote in tab context
     */
    public static final Icon TAB_EVERNOTE_ICON = PinNoteIcon.getSvgIconFromResource(
            "icons/evernote.svg", 0.5f
    ).orElse(AllIcons.Ide.FatalError);

    /**
     * Icon for local storage in tab context
     */
    public static final Icon TAB_LOCAL_NOTE_ICON = PinNoteIcon.getSvgIconFromResource(
            "icons/file.svg", 0.7f
    ).orElse(AllIcons.Ide.FatalError);

    /**
     * Icon for evernote in settings menu
     */
    public static final Icon SETTINGS_EVERNOTE_ICON = PinNoteIcon.getSvgIconFromResource(
            "icons/evernote.svg", 1.2f
    ).orElse(AllIcons.Ide.FatalError);

    /**
     * Icon for evernote in settings menu
     */
    public static final Icon SETTINGS_LOCAL_NOTE_ICON = PinNoteIcon.getSvgIconFromResource(
            "icons/file.svg", 1.5f
    ).orElse(AllIcons.Ide.FatalError);

    /**
     * Get Svg icon from resource
     *
     * @param resource name of resource
     * @param scale    float scale
     * @return Optional icon
     */
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
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
