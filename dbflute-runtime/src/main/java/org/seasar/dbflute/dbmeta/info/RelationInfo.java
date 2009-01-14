package org.seasar.dbflute.dbmeta.info;

import java.util.Map;

import org.seasar.dbflute.dbmeta.DBMeta;


/**
 * The class of referrer information.
 * @author DBFlute(AutoGenerator)
 */
public interface RelationInfo {

    public String getRelationPropertyName();

    public DBMeta getLocalDBMeta();

    public DBMeta getTargetDBMeta();

    public Map<ColumnInfo, ColumnInfo> getLocalTargetColumnInfoMap();

    public boolean isOneToOne();

    public boolean isReferrer();
}
