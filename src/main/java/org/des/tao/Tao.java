package org.des.tao;

import jsyntaxpane.DefaultSyntaxKit;
import org.des.tao.ide.ModelEditor;

import java.awt.EventQueue;

/**
 * Copyright Tao, All Rights Reserved.
 * Confidential, do not distribute.
 *
 * Any source code displaying this header must
 * be considered closed source and internal
 * until the project is released under an open
 * source license.
 */

public class Tao {
    public static void main(String[] args) {
        if (isOSX()) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Tao");
        }

        DefaultSyntaxKit.initKit();
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ModelEditor editor = new ModelEditor();
                    editor.getFrame().setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static boolean isOSX() {
        String osName = System.getProperty("os.name");
        return osName.contains("OS X");
    }
}
