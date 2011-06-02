/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.cbean.coption;

/**
 * The option for ScalarSelect. <br />
 * You can filter an aggregate function by scalar function filters.
 * @author jflute
 */
public class ScalarSelectOption extends FunctionConversionOption {

    // ===================================================================================
    //                                                                              Option
    //                                                                              ======
    /**
     * Set the value for coalesce function. <br />
     * If you set string value and the derived column is date type, it converts it to a date object internally.
     * For example, "2010-10-30 12:34:56.789", "2010/10/30" and so on ... are acceptable.
     * @param coalesce An alternate value when group function returns null. (NullAllowed: if null, no coalesce)
     * @return this. (NotNull)
     */
    public ScalarSelectOption coalesce(Object coalesce) {
        doCoalesce(coalesce);
        return this;
    }

    /**
     * Set the value for round function.
     * @param round Decimal digits or date format for round. (NullAllowed: if null, no round)
     * @return this. (NotNull)
     */
    public ScalarSelectOption round(Object round) {
        doRound(round);
        return this;
    }

    /**
     * Set the value for trunc function.
     * @param trunc Decimal digits or date format for trunc. (NullAllowed: if null, no trunc)
     * @return this. (NotNull)
     */
    public ScalarSelectOption trunc(Object trunc) {
        doTrunc(trunc);
        return this;
    }
}
