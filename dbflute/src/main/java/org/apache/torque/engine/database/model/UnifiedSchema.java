package org.apache.torque.engine.database.model;

import java.util.List;

import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfDatabaseProperties;
import org.seasar.dbflute.properties.DfLittleAdjustmentProperties;
import org.seasar.dbflute.properties.assistant.DfAdditionalSchemaInfo;
import org.seasar.dbflute.util.DfSystemUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class UnifiedSchema {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String NO_NAME_SCHEMA = "$$NoNameSchema$$"; // basically for MySQL

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _catalog;
    protected final String _schema;
    protected boolean _mainSchema;
    protected boolean _additionalSchema;
    protected boolean _unknownSchema;
    protected boolean _catalogAdditionalSchema;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected UnifiedSchema(String catalog, String schema) {
        if (isCompletelyUnsupportedDBMS()) {
            _catalog = null;
            _schema = null;
            return;
        }
        _catalog = filterAttribute(catalog);
        _schema = filterAttribute(schema);
    }

    protected UnifiedSchema(String schemaExpression) {
        if (isCompletelyUnsupportedDBMS()) {
            _catalog = null;
            _schema = null;
            return;
        }
        if (schemaExpression != null) {
            if (schemaExpression.contains(".")) {
                _catalog = filterAttribute(Srl.substringFirstFront(schemaExpression, "."));
                _schema = filterAttribute(Srl.substringFirstRear(schemaExpression, "."));
            } else {
                _catalog = null;
                _schema = filterAttribute(schemaExpression);
            }
        } else {
            _catalog = null;
            _schema = null;
        }
    }

    protected String filterAttribute(String element) {
        return Srl.is_NotNull_and_NotTrimmedEmpty(element) ? element.trim() : null;
    }

    protected boolean isCompletelyUnsupportedDBMS() {
        return getBasicProperties().isDatabaseAsUnifiedSchemaUnsupported();
    }

    // -----------------------------------------------------
    //                                               Creator
    //                                               -------
    public static UnifiedSchema createAsMainSchema(String catalog, String schema) {
        return new UnifiedSchema(catalog, schema).asMainSchema();
    }

    public static UnifiedSchema createAsAdditionalSchema(String catalog, String schema, boolean explicitCatalog) {
        final UnifiedSchema unifiedSchema = new UnifiedSchema(catalog, schema).asAdditionalSchema();
        if (explicitCatalog) {
            unifiedSchema.asCatalogAdditionalSchema();
        }
        return unifiedSchema;
    }

    public static UnifiedSchema createAsDynamicSchema(String catalog, String schema) {
        return new UnifiedSchema(catalog, schema).judgeSchema();
    }

    public static UnifiedSchema createAsDynamicSchema(String schemaExpression) {
        return new UnifiedSchema(schemaExpression).judgeSchema();
    }

    // -----------------------------------------------------
    //                                                Status
    //                                                ------
    protected UnifiedSchema asMainSchema() {
        _mainSchema = true;
        return this;
    }

    protected UnifiedSchema asAdditionalSchema() {
        _additionalSchema = true;
        return this;
    }

    protected UnifiedSchema asCatalogAdditionalSchema() {
        _catalogAdditionalSchema = true;
        return this;
    }

    protected UnifiedSchema asUnknownSchema() {
        _unknownSchema = true;
        return this;
    }

    protected UnifiedSchema judgeSchema() {
        final DfDatabaseProperties databaseProp = getDatabaseProperties();
        final UnifiedSchema mainSchema = databaseProp.getDatabaseSchema();
        if (equals(mainSchema)) {
            asMainSchema();
        } else {
            final DfAdditionalSchemaInfo info = databaseProp.getAdditionalSchemaInfo(this);
            if (info != null) {
                asAdditionalSchema();
                if (info.getUnifiedSchema().isCatalogAdditionalSchema()) {
                    asCatalogAdditionalSchema();
                }
            } else {
                asUnknownSchema();
            }
        }
        return this;
    }

    // ===================================================================================
    //                                                                   Schema Expression
    //                                                                   =================
    public String getCatalogSchema() {
        final StringBuilder sb = new StringBuilder();
        if (Srl.is_NotNull_and_NotTrimmedEmpty(_catalog)) {
            sb.append(_catalog);
        }
        if (Srl.is_NotNull_and_NotTrimmedEmpty(_schema)) {
            if (!isNoNameSchema()) {
                if (sb.length() > 0) {
                    sb.append(".");
                }
                sb.append(_schema);
            }
        }
        return sb.length() > 0 ? sb.toString() : null;
    }

    public String getIdentifiedSchema() {
        final StringBuilder sb = new StringBuilder();
        if (Srl.is_NotNull_and_NotTrimmedEmpty(_catalog)) {
            sb.append(_catalog);
        }
        if (sb.length() > 0) {
            sb.append(".");
        }
        if (Srl.is_NotNull_and_NotTrimmedEmpty(_schema)) {
            sb.append(_schema);
        } else {
            sb.append(NO_NAME_SCHEMA);
        }
        return sb.length() > 0 ? sb.toString() : null;
    }

    public String getLoggingSchema() {
        return getCatalogSchema();
    }

    public String getPureCatalog() {
        return _catalog;
    }

    public String getPureSchema() {
        if (isNoNameSchema()) {
            return null;
        }
        return _schema;
    }

    protected String getSqlPrefixSchema() {
        final DfLittleAdjustmentProperties prop = DfBuildProperties.getInstance().getLittleAdjustmentProperties();
        if (prop.isAvailableAddingSchemaToTableSqlName()) {
            if (prop.isAvailableAddingCatalogToTableSqlName()) {
                return getCatalogSchema();
            } else {
                return getPureSchema();
            }
        }
        if (_mainSchema) {
            return "";
        }
        if (_additionalSchema) {
            if (_catalogAdditionalSchema) {
                return getCatalogSchema();
            } else { // schema-only additional schema
                return getPureSchema();
            }
        }
        // additional drop or ReplaceSchema does not use this
        // (it uses an own connection so it does not need to qualify names)
        throwUnknownSchemaCannotUseSQLPrefixException();
        return null; // unreachable
    }

    protected void throwUnknownSchemaCannotUseSQLPrefixException() {
        final DfDatabaseProperties databaseProp = getDatabaseProperties();
        final UnifiedSchema databaseSchema = databaseProp.getDatabaseSchema();
        final List<UnifiedSchema> additionalSchemaList = databaseProp.getAdditionalSchemaList();
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Unknown schema is NOT supported to use SQL prefix.");
        br.addItem("Advice");
        br.addElement("The schema is NOT recognized as main and additional schema.");
        br.addElement("Please confirm your database settings.");
        br.addElement("(the schema must match any schema in target schemas)");
        br.addItem("Unknown Schema");
        br.addElement(toString());
        br.addItem("Target Schema");
        br.addElement(databaseSchema);
        for (UnifiedSchema additionalSchema : additionalSchemaList) {
            br.addElement(additionalSchema);
        }
        final String msg = br.buildExceptionMessage();
        throw new IllegalStateException(msg);
    }

    // ===================================================================================
    //                                                                 Unique Element Name
    //                                                                 ===================
    public String buildFullQualifiedName(String elementName) {
        return Srl.connectPrefix(elementName, getCatalogSchema(), ".");
    }

    public String buildSchemaQualifiedName(String elementName) {
        return Srl.connectPrefix(elementName, getPureSchema(), ".");
    }

    public String buildIdentifiedName(String elementName) {
        return Srl.connectPrefix(elementName, getIdentifiedSchema(), ".");
    }

    public String buildSqlName(String elementName) {
        final String sqlPrefixSchema = getSqlPrefixSchema();
        if (Srl.is_Null_or_TrimmedEmpty(sqlPrefixSchema)) {
            return null; // unreachable
        }
        return Srl.connectPrefix(elementName, sqlPrefixSchema, ".");
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean isMainSchema() {
        return _mainSchema;
    }

    public boolean isAdditionalSchema() {
        return _additionalSchema;
    }

    public boolean isUnknownSchema() {
        return _unknownSchema;
    }

    public boolean isCatalogAdditionalSchema() {
        return isAdditionalSchema() && _catalogAdditionalSchema;
    }

    public boolean hasSchema() {
        return Srl.is_NotNull_and_NotTrimmedEmpty(getCatalogSchema());
    }

    public boolean existsPureCatalog() {
        return Srl.is_NotNull_and_NotTrimmedEmpty(getPureCatalog());
    }

    public boolean existsPureSchema() {
        return Srl.is_NotNull_and_NotTrimmedEmpty(getPureSchema());
    }

    protected boolean isNoNameSchema() {
        return Srl.is_NotNull_and_NotTrimmedEmpty(_schema) && NO_NAME_SCHEMA.equalsIgnoreCase(_schema);
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UnifiedSchema)) {
            return false;
        }
        final String mySchema = getIdentifiedSchema();
        final String yourSchema = ((UnifiedSchema) obj).getIdentifiedSchema();
        if (mySchema == null && yourSchema == null) {
            return true;
        }
        return mySchema != null && mySchema.equalsIgnoreCase(yourSchema);
    }

    @Override
    public int hashCode() {
        final String identifiedSchema = getIdentifiedSchema();
        return identifiedSchema != null ? identifiedSchema.hashCode() : 17;
    }

    @Override
    public String toString() {
        return "{" + getIdentifiedSchema() + " as " + (isMainSchema() ? "main" : "")
                + (isAdditionalSchema() ? "additional" : "") + (isCatalogAdditionalSchema() ? "(catalog)" : "")
                + (isUnknownSchema() ? "unknown" : "") + "}";
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfBasicProperties getBasicProperties() {
        return DfBuildProperties.getInstance().getBasicProperties();
    }

    protected DfDatabaseProperties getDatabaseProperties() {
        return DfBuildProperties.getInstance().getDatabaseProperties();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }
}
