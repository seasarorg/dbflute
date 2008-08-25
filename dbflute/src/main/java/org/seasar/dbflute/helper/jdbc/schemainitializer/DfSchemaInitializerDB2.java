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
package org.seasar.dbflute.helper.jdbc.schemainitializer;

import org.seasar.dbflute.helper.jdbc.metadata.info.DfTableMetaInfo;

/**
 * The schema initializer for DB2.
 * @author jflute
 * @since 0.7.9 (2008/08/24 Monday)
 */
public class DfSchemaInitializerDB2 extends DfSchemaInitializerJdbc {

    @Override
    protected void setupDropTable(StringBuilder sb, DfTableMetaInfo metaInfo) {
        if (metaInfo.isTableTypeAlias()) {
            sb.append("drop alias ").append(metaInfo.getTableName());
        } else {
            super.setupDropTable(sb, metaInfo);
        }
    }
}