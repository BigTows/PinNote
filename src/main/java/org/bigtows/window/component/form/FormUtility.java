/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.window.component.form;

import com.intellij.util.ui.UIUtil;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * The type Form utility.
 */
public class FormUtility {

    /**
     * Gets scaled image.
     *
     * @param sourceImage the source image
     * @param w           the w
     * @param h           the h
     * @return the scaled image
     */
    public static Image getScaledImage(Image sourceImage, int w, int h) {
        BufferedImage newImage = UIUtil.createImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = newImage.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(sourceImage, 0, 0, w, h, null);
        g2.dispose();

        return newImage;
    }
}
