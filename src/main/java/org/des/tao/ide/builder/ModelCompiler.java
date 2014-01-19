package org.des.tao.ide.builder;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Copyright Tao, All Rights Reserved.
 * Confidential, do not distribute.
 *
 * Any source code displaying this header must
 * be considered closed source and confidential
 * until the project is released under an open
 * source license.
 */

public class ModelCompiler {
    public static final JavaCompiler JAVA_COMPILER =
            ToolProvider.getSystemJavaCompiler();

    public static void compileModel(File modelSource) throws IOException {
        DiagnosticCollector<JavaFileObject> diagnostics =
                new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager fileManager =
                JAVA_COMPILER.getStandardFileManager(diagnostics, null, null);
        String[] compilerOptions = new String[]{
                "-d", "tmp/out"
        };

        Iterable<? extends JavaFileObject> sourceObjects =
                fileManager.getJavaFileObjects(modelSource);

        JavaCompiler.CompilationTask compilationTask = JAVA_COMPILER.getTask(
                null, null, null, Arrays.asList(compilerOptions), null, sourceObjects);
        compilationTask.call();
        fileManager.close();
    }
}
