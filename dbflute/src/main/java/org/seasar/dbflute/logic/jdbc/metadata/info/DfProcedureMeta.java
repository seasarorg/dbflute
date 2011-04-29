package org.seasar.dbflute.logic.jdbc.metadata.info;

import java.util.ArrayList;
import java.util.List;

import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfDocumentProperties;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class DfProcedureMeta {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _procedureCatalog;
    protected UnifiedSchema _procedureSchema;
    protected String _procedureName; // contains package prefix
    protected DfProcedureType _procedureType;
    protected String _procedureFullQualifiedName;
    protected String _procedureSchemaQualifiedName;
    protected String _procedureSqlName; // basically for procedure synonym
    protected String _procedureComment;
    protected String _procedurePackage; // basically for dropping procedure
    protected boolean _procedureSynonym;
    protected boolean _packageProcedure;

    protected final List<DfProcedureColumnMeta> _procedureColumnList = new ArrayList<DfProcedureColumnMeta>();
    protected final List<DfProcedureNotParamResultMeta> _notParamResultList = new ArrayList<DfProcedureNotParamResultMeta>();

    // ===================================================================================
    //                                                                          Expression
    //                                                                          ==========
    public String getProcedureDisplayNameForSchemaHtml() {
        final StringBuilder sb = new StringBuilder();
        sb.append(buildProcedureSqlName());
        final String typeDisp = _procedureType.alias() + (_procedureSynonym ? ", Synonym" : "");
        sb.append(" <span class=\"type\">(").append(typeDisp).append(")</span>");
        return sb.toString();
    }

    public boolean hasProcedureComment() {
        return Srl.is_NotNull_and_NotTrimmedEmpty(_procedureComment);
    }

    public String getProcedureCommentForSchemaHtml() {
        final DfDocumentProperties prop = DfBuildProperties.getInstance().getDocumentProperties();
        String comment = _procedureComment;
        comment = prop.resolvePreTextForSchemaHtml(comment);
        return comment;
    }

    public boolean isPackageProcdure() {
        return Srl.is_NotNull_and_NotTrimmedEmpty(_procedurePackage);
    }

    // -----------------------------------------------------
    //                                            Build Name
    //                                            ----------
    public String buildProcedureKeyName() {
        return _procedureName;
    }

    public String buildProcedureLoggingName() {
        return _procedureFullQualifiedName;
    }

    public String buildProcedureSqlName() {
        if (Srl.is_NotNull_and_NotTrimmedEmpty(_procedureSqlName)) {
            return _procedureSqlName;
        }
        final DfBasicProperties prop = DfBuildProperties.getInstance().getBasicProperties();
        final String sqlName = getProcedureSchema().buildSqlName(getProcedureName());
        if (prop.isDatabaseDB2() && !sqlName.contains(".")) { // patch
            // DB2 needs schema prefix for calling procedures
            // (actually executed and confirmed result)
            _procedureSqlName = getProcedureSchema().buildSchemaQualifiedName(sqlName);
        } else {
            _procedureSqlName = sqlName;
        }
        if (prop.isDatabaseSQLServer()) {
            // SQLServer returns 'sp_foo;1'
            _procedureSqlName = Srl.substringLastFront(_procedureSqlName, ";");
        }
        return _procedureSqlName;
    }

    public String buildProcedurePureName() {
        return Srl.substringLastRear(_procedureName, ".");
    }

    // ===================================================================================
    //                                                                    Bind Information
    //                                                                    ================
    public int getBindParameterCount() {
        int count = 0;
        for (DfProcedureColumnMeta columnInfo : _procedureColumnList) {
            if (columnInfo.isBindParameter()) {
                ++count;
            }
        }
        return count;
    }

    public int getInputParameterCount() {
        int count = 0;
        for (DfProcedureColumnMeta columnInfo : _procedureColumnList) {
            if (columnInfo.isInputParameter()) {
                ++count;
            }
        }
        return count;
    }

    // ===================================================================================
    //                                                             Execution Determination
    //                                                             =======================
    // -----------------------------------------------------
    //                                               Calling
    //                                               -------
    public boolean isCalledBySelect() {
        // SQLServer's table valued function cannot be called normally
        // (whether that others like this exist or not is unknown for now)
        return isSQLServerTableValuedFunction();
    }

    // -----------------------------------------------------
    //                                              Overload
    //                                              --------
    public boolean hasOverloadParameter() {
        for (DfProcedureColumnMeta columnInfo : _procedureColumnList) {
            if (columnInfo.getOverloadNo() != null) {
                return true;
            }
        }
        return false;
    }

    // ===================================================================================
    //                                                              Pinpoint Determination
    //                                                              ======================
    public boolean isSQLServerTableValuedFunction() {
        if (!getBasicProperties().isDatabaseSQLServer()) {
            return false;
        }
        for (DfProcedureColumnMeta columnInfo : _procedureColumnList) {
            if (columnInfo.isSQLServerTableReturnValue()) {
                return true;
            }
        }
        return false;
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    protected DfBasicProperties getBasicProperties() {
        return DfBuildProperties.getInstance().getBasicProperties();
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return "{" + _procedureFullQualifiedName + ", " + _procedureType + ", " + _procedureComment + ", "
                + _procedureColumnList + ", notParamResult=" + _notParamResultList.size() + "}";
    }

    // ===================================================================================
    //                                                                      Procedure Type
    //                                                                      ==============
    public enum DfProcedureType {
        procedureResultUnknown("ResultUnknown"), procedureNoResult("NoResult"), procedureReturnsResult("ReturnsResult");
        private String _alias;

        private DfProcedureType(String alias) {
            _alias = alias;
        }

        public String alias() {
            return _alias;
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getProcedureCatalog() {
        return _procedureCatalog;
    }

    public void setProcedureCatalog(String procedureCatalog) {
        this._procedureCatalog = procedureCatalog;
    }

    public UnifiedSchema getProcedureSchema() {
        return _procedureSchema;
    }

    public void setProcedureSchema(UnifiedSchema procedureSchema) {
        this._procedureSchema = procedureSchema;
    }

    public String getProcedureName() {
        return _procedureName;
    }

    public void setProcedureName(String procedureName) {
        this._procedureName = procedureName;
    }

    public DfProcedureType getProcedureType() {
        return _procedureType;
    }

    public void setProcedureType(DfProcedureType procedureType) {
        this._procedureType = procedureType;
    }

    public String getProcedureFullQualifiedName() {
        return _procedureFullQualifiedName;
    }

    public void setProcedureFullQualifiedName(String procedureFullQualifiedName) {
        this._procedureFullQualifiedName = procedureFullQualifiedName;
    }

    public String getProcedureSchemaQualifiedName() {
        return _procedureSchemaQualifiedName;
    }

    public void setProcedureSchemaQualifiedName(String procedureSchemaQualifiedName) {
        this._procedureSchemaQualifiedName = procedureSchemaQualifiedName;
    }

    protected void setProcedureSqlName(String procedureSqlName) { // basically for procedure synonym
        this._procedureSqlName = procedureSqlName;
    }

    public String getProcedureComment() {
        return _procedureComment;
    }

    public void setProcedureComment(String procedureComment) {
        this._procedureComment = procedureComment;
    }

    public String getProcedurePackage() {
        return _procedurePackage;
    }

    public void setProcedurePackage(String procedurePackage) {
        this._procedurePackage = procedurePackage;
    }

    public boolean isProcedureSynonym() {
        return _procedureSynonym;
    }

    public void setProcedureSynonym(boolean procedureSynonym) {
        this._procedureSynonym = procedureSynonym;
    }

    public List<DfProcedureColumnMeta> getProcedureColumnList() {
        return _procedureColumnList;
    }

    public void addProcedureColumn(DfProcedureColumnMeta procedureColumn) {
        _procedureColumnList.add(procedureColumn);
    }

    public List<DfProcedureNotParamResultMeta> getNotParamResultList() {
        return _notParamResultList;
    }

    public void addNotParamResult(DfProcedureNotParamResultMeta notParamResult) {
        this._notParamResultList.add(notParamResult);
    }
}
