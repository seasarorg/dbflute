/*
 * Copyright 2004-2012 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.dbmeta.info;

import java.util.Map;

import org.seasar.dbflute.dbmeta.DBMeta;

/**
 * The class of relation information.
 * @author jflute
 */
public interface RelationInfo {

    /**
     * Get the name of the relation constraint. <br />
     * The foreign info and the referrer info of the same relation have the same name.
     * @return The name of the relation constraint. (NotNull)
     */
    String getConstraintName();

    /**
     * Get the property name of the relation. <br />
     * This is unique name in the table.
     * @return The property name of the relation. (NotNull)
     */
    String getRelationPropertyName();

    DBMeta getLocalDBMeta();

    DBMeta getTargetDBMeta();

    Map<ColumnInfo, ColumnInfo> getLocalTargetColumnInfoMap();

    /**
     * Does the relation is one-to-one?
     * @return The determination, true or false.
     */
    boolean isOneToOne();

    /**
     * Does the relation is referrer?
     * @return The determination, true or false.
     */
    boolean isReferrer();

    /**
     * Get the relation info of reverse relation.
     * @return The instance of relation info. (NullAllowed: if null, means one-way reference)
     */
    RelationInfo getReverseRelation();
}
