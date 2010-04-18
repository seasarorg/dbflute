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
public class DfProcedureMetaInfo {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _procedureCatalog;
    protected UnifiedSchema _procedureSchema;
    protected String _procedureName;
    protected DfProcedureType _procedureType;
    protected String _procedureFullQualifiedName;
    protected String _procedureSchemaQualifiedName;
    protected String _procedureSqlName; // basically for procedure synonym
    protected String _procedureComment;
    protected boolean _procedureSynonym;

    protected List<DfProcedureColumnMetaInfo> _procedureColumnList = new ArrayList<DfProcedureColumnMetaInfo>();
    protected List<DfProcedureNotParamResultMetaInfo> _notParamResultList = new ArrayList<DfProcedureNotParamResultMetaInfo>();

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

    public String buildProcedureLoggingName() {
        return _procedureFullQualifiedName;
    }

    public String buildProcedureSqlName() {
        if (Srl.is_NotNull_and_NotTrimmedEmpty(_procedureSqlName)) {
            return _procedureSqlName;
        }
        final DfBasicProperties prop = DfBuildProperties.getInstance().getBasicProperties();
        final String procedureName = getProcedureName();
        if (prop.isDatabaseOracle() && procedureName.contains(".")) { // package procedure
            // returns plain name because it cannot add schema prefix to package procedure 
            _procedureSqlName = procedureName;
            return _procedureSqlName;
        }
        final String sqlName = getProcedureSchema().buildSqlName(procedureName);
        // DB2 needs schema prefix for calling procedures. (actually tried)
        if (prop.isDatabaseDB2() && !sqlName.contains(".")) {
            _procedureSqlName = Srl.connectPrefix(sqlName, getProcedureSchema().getPureSchema(), ".");
        } else {
            _procedureSqlName = sqlName;
        }
        return _procedureSqlName;
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

    public String getProcedureSqlName() {
        return _procedureSqlName;
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

    public boolean isProcedureSynonym() {
        return _procedureSynonym;
    }

    public void setProcedureSynonym(boolean procedureSynonym) {
        this._procedureSynonym = procedureSynonym;
    }

    public List<DfProcedureColumnMetaInfo> getProcedureColumnList() {
        return _procedureColumnList;
    }

    public void addProcedureColumnMetaInfo(DfProcedureColumnMetaInfo procedureColumn) {
        _procedureColumnList.add(procedureColumn);
    }

    public List<DfProcedureNotParamResultMetaInfo> getNotParamResultList() {
        return _notParamResultList;
    }

    public void addNotParamResult(DfProcedureNotParamResultMetaInfo notParamResult) {
        this._notParamResultList.add(notParamResult);
    }
}
