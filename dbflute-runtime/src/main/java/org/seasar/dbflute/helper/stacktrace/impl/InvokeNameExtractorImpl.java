/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.helper.stacktrace.impl;

import org.seasar.dbflute.helper.stacktrace.InvokeNameExtractingResource;
import org.seasar.dbflute.helper.stacktrace.InvokeNameExtractor;
import org.seasar.dbflute.helper.stacktrace.InvokeNameResult;

/**
 * @author jflute
 */
public class InvokeNameExtractorImpl implements InvokeNameExtractor {

    // ==========================================================================================
    //                                                                                  Attribute
    //                                                                                  =========
    protected StackTraceElement[] _stackTrace;

    // ==========================================================================================
    //                                                                                       Main
    //                                                                                       ====
    /**
     * @param resource the call-back resource for invoke-name-extracting. (NotNull)
     * @return Invoke name. (NotNull: If not found, returns empty string.)
     */
    public InvokeNameResult extractInvokeName(InvokeNameExtractingResource resource) {
        if (_stackTrace == null) {
            String msg = "The attribute 'stackTrace' should not be null: resource=" + resource;
            throw new IllegalStateException(msg);
        }
        String targetSimpleClassName = null;
        String targetMethodName = null;
        int lineNumber = 0;
        int foundIndex = -1; // The minus one means 'Not Found'.
        int foundFirstIndex = -1; // The minus one means 'Not Found'.
        boolean onTarget = false;
        for (int i = resource.getStartIndex(); i < _stackTrace.length; i++) {
            final StackTraceElement element = _stackTrace[i];
            if (i > resource.getStartIndex() + resource.getLoopSize()) {
                break;
            }
            final String className = element.getClassName();
            if (className.startsWith("sun.") || className.startsWith("java.")) {
                if (onTarget) {
                    break;
                }
                continue;
            }
            final String methodName = element.getMethodName();
            if (resource.isTargetElement(className, methodName)) {
                if (methodName.equals("invoke")) {
                    continue;
                }
                targetSimpleClassName = className.substring(className.lastIndexOf(".") + 1);
                targetMethodName = methodName;
                if (resource.isUseAdditionalInfo()) {
                    lineNumber = element.getLineNumber();
                }
                foundIndex = i;
                if (foundFirstIndex == -1) {
                    foundFirstIndex = i;
                }
                onTarget = true;
                if (resource.isBreakAtFirstElement()) {
                    break;
                } else {
                    continue;
                }
            }
            if (onTarget) {
                break;
            }
        }
        final InvokeNameResult result = new InvokeNameResult();
        if (targetSimpleClassName == null) {
            result.beEmptyResult(); // Not Found! It sets empty result.
            return result;
        }
        final String filteredClassName = resource.filterSimpleClassName(targetSimpleClassName);
        result.setSimpleClassName(resource.filterSimpleClassName(targetSimpleClassName));
        result.setMethodName(targetMethodName);
        if (lineNumber > 0) {
            result.setInvokeName(filteredClassName + "." + targetMethodName + "():" + lineNumber + " --> ");
        } else {
            result.setInvokeName(filteredClassName + "." + targetMethodName + "() --> ");
        }
        result.setFoundIndex(foundIndex);
        result.setFoundFirstIndex(foundFirstIndex);
        return result;
    }

    // ==========================================================================================
    //                                                                                   Accessor
    //                                                                                   ========
    public void setStackTrace(StackTraceElement[] stackTrace) {
        _stackTrace = stackTrace;
    }
}