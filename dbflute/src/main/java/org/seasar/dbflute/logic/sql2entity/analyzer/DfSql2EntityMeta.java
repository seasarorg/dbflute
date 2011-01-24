package org.seasar.dbflute.logic.sql2entity.analyzer;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.seasar.dbflute.logic.sql2entity.cmentity.DfCustomizeEntityInfo;
import org.seasar.dbflute.logic.sql2entity.pmbean.DfPmbMetaData;
import org.seasar.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 */
public class DfSql2EntityMeta {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    // cursor info is actually only flag, use cursor or not
    public static final Object CURSOR_INFO_DUMMY = new Object();

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // key=entityName
    // an entity name for a map key is a name not resolved about project prefix
    // (the prefix is resolved in Table class)
    protected final Map<String, DfCustomizeEntityInfo> _entityInfoMap = DfCollectionUtil.newLinkedHashMap();
    protected final Map<String, Object> _cursorInfoMap = DfCollectionUtil.newLinkedHashMap();
    protected final Map<String, File> _entitySqlFileMap = DfCollectionUtil.newLinkedHashMap();
    protected final Map<String, List<String>> _primaryKeyMap = DfCollectionUtil.newLinkedHashMap();

    // key=pmbName
    protected final Map<String, DfPmbMetaData> _pmbMetaDataMap = DfCollectionUtil.newLinkedHashMap();

    // key=fileName
    protected final Map<String, String> _exceptionInfoMap = DfCollectionUtil.newLinkedHashMap();

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Map<String, DfCustomizeEntityInfo> getEntityInfoMap() {
        return _entityInfoMap;
    }

    public void addEntityInfo(String entityName, DfCustomizeEntityInfo entityInfo) {
        _entityInfoMap.put(entityName, entityInfo);
    }

    public Map<String, Object> getCursorInfoMap() {
        return _cursorInfoMap;
    }

    public void addCursorInfo(String entityName, Object cursorInfo) {
        _cursorInfoMap.put(entityName, cursorInfo);
    }

    public Map<String, File> getEntitySqlFileMap() {
        return _entitySqlFileMap;
    }

    public void addEntitySqlFile(String entityName, File entitySqlFile) {
        _entitySqlFileMap.put(entityName, entitySqlFile);
    }

    public Map<String, List<String>> getPrimaryKeyMap() {
        return _primaryKeyMap;
    }

    public void addPrimaryKey(String entityName, List<String> primaryKeyList) {
        _primaryKeyMap.put(entityName, primaryKeyList);
    }

    public Map<String, DfPmbMetaData> getPmbMetaDataMap() {
        return _pmbMetaDataMap;
    }

    public void addPmbMetaData(String pmbName, DfPmbMetaData pmbMetaData) {
        _pmbMetaDataMap.put(pmbName, pmbMetaData);
    }

    public Map<String, String> getExceptionInfoMap() {
        return _exceptionInfoMap;
    }

    public void addExceptionInfo(String fileName, String exceptionInfo) {
        _exceptionInfoMap.put(fileName, exceptionInfo);
    }
}
