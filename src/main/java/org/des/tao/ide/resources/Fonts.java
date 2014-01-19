package org.des.tao.ide.resources;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

/**
 * Copyright Tao, All Rights Reserved.
 * Confidential, do not distribute.
 *
 * Any source code displaying this header must
 * be considered closed source and confidential
 * until the project is released under an open
 * source license.
 */

public class Fonts {
    public static final Font DIALOG = new Font("Dialog", Font.PLAIN, 12);

    public static final FontRenderContext DEFAULT_RENDER_CONTEXT =
            new FontRenderContext(new AffineTransform(), true, true);
}
