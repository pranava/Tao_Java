package org.des.tao.ide.legacy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

/**
 * Copyright Tao, All Rights Reserved.
 * Confidential, do not distribute.
 *
 * Any source code displaying this header must
 * be considered closed source and confidential
 * until the project is released under an open
 * source license.
 */

public class SigmaParser {

    public enum Section {
        STATE_VARIABLES("STATE VARIABLES"),
        VERTICIES("VERTICES"),
        EDGES("EDGES");

        private String identifier;

        private Section(String identifier) {
            this.identifier = identifier;
        }

        public String getIdentifier() {
            return identifier;
        }

        public static Section findSectionByIdentifier(String identifier) {
            for (Section section: values()) {
                if (section.getIdentifier().equals(identifier)) {
                    return section;
                }
            }

            return null;
        }
    }

    public static void main(String[] args) {
        File modFile = new File("CARWASH.MOD");
        try {
            Scanner modScanner = new Scanner(new FileReader(modFile));
            while (modScanner.hasNextLine()) {
                System.out.println(modScanner.nextLine());
            }
        } catch (FileNotFoundException e) {}
    }


}
