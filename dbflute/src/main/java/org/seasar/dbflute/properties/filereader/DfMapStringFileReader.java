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
package org.seasar.dbflute.properties.filereader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.seasar.dbflute.exception.DfMapStringFileReadFailureException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.infra.dfprop.DfPropFile;

/**
 * @author jflute
 */
public class DfMapStringFileReader {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DfPropFile _dfpropFile = new DfPropFile();

    // ===================================================================================
    //                                                                                Read
    //                                                                                ====
    public Map<String, Object> readMap(String path) {
        FileInputStream ins = null;
        try {
            ins = new FileInputStream(new File(path));
            return _dfpropFile.readMap(ins);
        } catch (FileNotFoundException e) {
            return newLinkedHashMap();
        } catch (RuntimeException e) {
            throwMapStringFileReadFailureException(path, e);
            return null; // unreachable
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public Map<String, String> readMapAsStringValue(String path) {
        FileInputStream ins = null;
        try {
            ins = new FileInputStream(new File(path));
            return _dfpropFile.readMapAsStringValue(ins);
        } catch (FileNotFoundException e) {
            return newLinkedHashMap();
        } catch (RuntimeException e) {
            throwMapStringFileReadFailureException(path, e);
            return null; // unreachable
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public Map<String, List<String>> readMapAsStringListValue(String path) {
        FileInputStream ins = null;
        try {
            ins = new FileInputStream(new File(path));
            return _dfpropFile.readMapAsStringListValue(ins);
        } catch (FileNotFoundException e) {
            return newLinkedHashMap();
        } catch (RuntimeException e) {
            throwMapStringFileReadFailureException(path, e);
            return null; // unreachable
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public Map<String, Map<String, String>> readMapAsStringMapValue(String path) {
        FileInputStream ins = null;
        try {
            ins = new FileInputStream(new File(path));
            return _dfpropFile.readMapAsStringMapValue(ins);
        } catch (FileNotFoundException e) {
            return newLinkedHashMap();
        } catch (RuntimeException e) {
            throwMapStringFileReadFailureException(path, e);
            return null; // unreachable
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    protected <KEY, VALUE> LinkedHashMap<KEY, VALUE> newLinkedHashMap() {
        return new LinkedHashMap<KEY, VALUE>();
    }

    protected void throwMapStringFileReadFailureException(String path, RuntimeException e) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Failed to read the map-string file.");
        br.addItem("Advice");
        br.addElement("Make sure the map-string is correct in the file.");
        br.addElement("For exapmle, the number of start and end braces are the same.");
        br.addItem("MapString File");
        br.addElement(path);
        final String msg = br.buildExceptionMessage();
        throw new DfMapStringFileReadFailureException(msg, e);
    }

    // ===================================================================================
    //                                                                              Option
    //                                                                              ======
    public DfMapStringFileReader saveLine() {
        _dfpropFile.saveLine();
        return this;
    }
}