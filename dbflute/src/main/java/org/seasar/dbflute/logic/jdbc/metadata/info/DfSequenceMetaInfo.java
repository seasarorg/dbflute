/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.logic.jdbc.metadata.info;

/**
 * @author jflute
 */
public class DfSequenceMetaInfo {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String sequenceOwner;
    protected String sequenceName;
    protected Integer minValue;
    protected Integer maxValue;
    protected Integer incrementSize;

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return sequenceOwner + "." + sequenceName + ":{" + minValue + " to " + maxValue + ", increment "
                + incrementSize + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========

    public String getSequenceOwner() {
        return sequenceOwner;
    }

    public void setSequenceOwner(String sequenceOwner) {
        this.sequenceOwner = sequenceOwner;
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    public Integer getMinValue() {
        return minValue;
    }

    public void setMinValue(Integer minValue) {
        this.minValue = minValue;
    }

    public Integer getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Integer maxValue) {
        this.maxValue = maxValue;
    }

    public Integer getIncrementSize() {
        return incrementSize;
    }

    public void setIncrementSize(Integer incrementSize) {
        this.incrementSize = incrementSize;
    }
}
