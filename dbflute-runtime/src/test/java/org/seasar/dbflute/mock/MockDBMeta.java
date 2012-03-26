package org.seasar.dbflute.mock;

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.PropertyGateway;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.ForeignInfo;
import org.seasar.dbflute.dbmeta.info.ReferrerInfo;
import org.seasar.dbflute.dbmeta.info.RelationInfo;
import org.seasar.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.dbflute.dbmeta.name.TableSqlName;

public class MockDBMeta extends AbstractDBMeta {

    public DBDef getCurrentDBDef() {
        return null;
    }

    public String findDbName(String flexibleName) {
        return null;
    }

    public DBMeta findForeignDBMeta(String foreignPropName) {
        return null;
    }

    public ForeignInfo findForeignInfo(String foreignPropName) {
        return null;
    }

    public String findPropertyName(String flexibleName) {
        return null;
    }

    public DBMeta findReferrerDBMeta(String referrerPropertyName) {
        return null;
    }

    public ReferrerInfo findReferrerInfo(String referrerPropertyName) {
        return null;
    }

    public RelationInfo findRelationInfo(String relationPropertyName) {
        return null;
    }

    public String getBehaviorTypeName() {
        return null;
    }

    public List<ColumnInfo> getColumnInfoList() {
        return null;
    }

    public String getConditionBeanTypeName() {
        return null;
    }

    public String getDaoTypeName() {
        return null;
    }

    public Class<? extends Entity> getEntityType() {
        return null;
    }

    public String getEntityTypeName() {
        return null;
    }

    public UniqueInfo getPrimaryUniqueInfo() {
        return null;
    }

    public String getTableDbName() {
        return null;
    }

    public String getTablePropertyName() {
        return null;
    }

    public TableSqlName getTableSqlName() {
        return null;
    }

    public boolean hasPrimaryKey() {
        return false;
    }

    public boolean hasCompoundPrimaryKey() {
        return false;
    }

    public Entity newEntity() {
        return null;
    }

    @Override
    protected List<ColumnInfo> ccil() {
        return null;
    }

    public void acceptPrimaryKeyMap(Entity entity, Map<String, ? extends Object> primaryKeyMap) {
    }

    public void acceptAllColumnMap(Entity entity, Map<String, ? extends Object> allColumnMap) {
    }

    public Map<String, Object> extractPrimaryKeyMap(Entity entity) {
        return null;
    }

    public Map<String, Object> extractAllColumnMap(Entity entity) {
        return null;
    }

    public PropertyGateway findPropertyGateway(String propertyName) {
        return null;
    }
}
