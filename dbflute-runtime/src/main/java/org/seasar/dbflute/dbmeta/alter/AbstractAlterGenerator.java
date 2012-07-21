package org.seasar.dbflute.dbmeta.alter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.info.ForeignInfo;

/**
 * @author jflute
 */
public abstract class AbstractAlterGenerator implements AlterGenerator {

    // ===================================================================================
    //                                                                               Table
    //                                                                               =====
    protected void doBuildAlterTable(StringBuilder sb, DBMeta dbmeta) {
        doBuildAlterTable(sb, dbmeta.getTableSqlName().toString());
    }

    protected void doBuildAlterTable(StringBuilder sb, String tableName) {
        sb.append("alter table ").append(tableName);
    }

    // ===================================================================================
    //                                                                              Column
    //                                                                              ======

    // ===================================================================================
    //                                                                         Primary Key
    //                                                                         ===========

    // ===================================================================================
    //                                                                         Foreign Key
    //                                                                         ===========
    public List<String> packForeignAddList(Collection<DBMeta> dbmetaList) {
        final List<String> ddlList = new ArrayList<String>();
        for (DBMeta dbmeta : dbmetaList) {
            final List<ForeignInfo> foreignInfoList = dbmeta.getForeignInfoList();
            for (ForeignInfo info : foreignInfoList) {
                if (info.isPureFK()) {
                    ddlList.add(generateForeignKeyAdd(info));
                }
            }
        }
        return ddlList;
    }

    public List<String> packForeignDropList(Collection<DBMeta> dbmetaList) {
        final List<String> ddlList = new ArrayList<String>();
        for (DBMeta dbmeta : dbmetaList) {
            final List<ForeignInfo> foreignInfoList = dbmeta.getForeignInfoList();
            for (ForeignInfo info : foreignInfoList) {
                if (info.isPureFK()) {
                    ddlList.add(generateForeignKeyDrop(info));
                }
            }
        }
        return ddlList;
    }
}
