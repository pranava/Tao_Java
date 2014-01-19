package org.des.tao.ide.resources;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;

import java.io.File;
import java.io.IOException;

/**
 * Copyright Tao, All Rights Reserved.
 * Confidential, do not distribute.
 *
 * Any source code displaying this header must
 * be considered closed source and confidential
 * until the project is released under an open
 * source license.
 */

public class Templates {
    private static Templates templatesInstance = new Templates();

    private Configuration templateConfiguration;

    private Templates() {
        templateConfiguration = new Configuration();
        try {
            templateConfiguration.setDirectoryForTemplateLoading(
                new File("resources/templates"));
            templateConfiguration.setObjectWrapper(new DefaultObjectWrapper());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Configuration getConfiguration() {
        return templateConfiguration;
    }

    public static Templates getTemplatesInstance() {
        return templatesInstance;
    }

}
