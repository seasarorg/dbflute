/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.helper.filesystem;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @author jflute
 */
public class FileHierarchyTracer {

    /**
     * Trace the files in the hierarchy from the root directory.
     * <pre>
     * tracer.trace(srcDir, new FileHierarchyTracingHandler() {
     *     public boolean isTargetFileOrDir(File currentFile) {
     *         return currentFile.isDirectory() || currentFile.getName().endsWith(".java");
     *     }
     *     public void handleFile(File currentFile) {
     *         ...
     *     }
     * }
     * </pre>
     * @param rootDir The directory for root to trace files. (NotNull)
     * @param handler The handler of tracing. (NotNull)
     */
    public void trace(File rootDir, FileHierarchyTracingHandler handler) {
        doTrace(rootDir, handler);
    }

    protected void doTrace(File currentDir, final FileHierarchyTracingHandler handler) {
        final File[] listFiles = currentDir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return handler.isTargetFileOrDir(file);
            }
        });
        if (listFiles == null) {
            return;
        }
        orderListFiles(listFiles); // to be same order between Windows and MacOSX
        for (File elementFile : listFiles) {
            if (elementFile.isDirectory()) {
                doTrace(elementFile, handler);
            } else {
                handler.handleFile(elementFile);
            }
        }
    }

    protected void orderListFiles(File[] listFiles) {
        Arrays.sort(listFiles, new Comparator<File>() {
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }
}
