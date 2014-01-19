package org.des.tao.ide.editors;

/**
 * Copyright Tao, All Rights Reserved.
 * Confidential, do not distribute.
 *
 * Any source code displaying this header must
 * be considered closed source and confidential
 * until the project is released under an open
 * source license.
 */

public interface Versionable {
    public void commitChanges();
    public void revertChanges();

    public void initialize();
}
