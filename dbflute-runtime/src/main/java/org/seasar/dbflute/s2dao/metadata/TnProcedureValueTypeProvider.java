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
package org.seasar.dbflute.s2dao.metadata;

import java.util.List;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.jdbc.ValueType;
import org.seasar.dbflute.s2dao.valuetype.TnValueTypes;

/**
 * @author jflute
 */
public class TnProcedureValueTypeProvider {

    // ===================================================================================
    //                                                                                Main
    //                                                                                ====
    public ValueType provideValueType(final String name, Class<?> type, DBDef currentDBDef) {
        if (name != null) {
            return TnValueTypes.getPluginValueType(name);
        }
        if (List.class.isAssignableFrom(type)) { // is for out parameter cursor.
            if (DBDef.PostgreSQL.equals(currentDBDef)) {
                return TnValueTypes.POSTGRESQL_RESULT_SET;
            } else if (DBDef.Oracle.equals(currentDBDef)) {
                return TnValueTypes.ORACLE_RESULT_SET;
            } else {
                return TnValueTypes.SERIALIZABLE_BYTE_ARRAY;
            }
        }
        return TnValueTypes.getValueType(type);
    }
}
