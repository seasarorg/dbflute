package org.seasar.dbflute.dbmeta.alter;

import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.ForeignInfo;

/**
 * @author jflute
 */
public interface AlterGenerator {

    // ===================================================================================
    //                                                                               Table
    //                                                                               =====
    String generateTableRename(String oldTableName, DBMeta newMeta);

    String generateTableRename(DBMeta oldMeta, String newTableName);

    // ===================================================================================
    //                                                                              Column
    //                                                                              ======
    String generateColumnDefChange(ColumnInfo columnInfo);

    String generateColumnRename(String oldColumnName, ColumnInfo newColumnInfo);

    String generateColumnRename(ColumnInfo oldColumnInfo, String newColumnName);

    // ===================================================================================
    //                                                                         Primary Key
    //                                                                         ===========
    String generatePrimaryKeyAdd(ColumnInfo columnInfo);

    String generatePrimaryKeyDrop(DBMeta dbmeta);

    // ===================================================================================
    //                                                                         Foreign Key
    //                                                                         ===========
    String generateForeignKeyAdd(ForeignInfo foreignInfo);

    String generateForeignKeyDrop(ForeignInfo foreignInfo);
}
