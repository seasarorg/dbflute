package org.apache.torque.engine.database.model;

import org.seasar.dbflute.properties.DfDatabaseProperties;
import org.seasar.dbflute.properties.assistant.DfAdditionalSchemaInfo;
import org.seasar.dbflute.util.DfTypeUtil;
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
        _catalog = filterAttribute(catalog);
        _schema = filterAttribute(schema);
    }

    protected UnifiedSchema(String schemaExpression) {
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

    public static UnifiedSchema createAsMainSchema(String catalog, String schema) {
        return new UnifiedSchema(catalog, schema).asMainSchema();
    }

    public static UnifiedSchema createAsAdditionalSchema(String catalog, String schema, boolean catalogSpecified) {
        final UnifiedSchema unifiedSchema = new UnifiedSchema(catalog, schema);
        if (catalogSpecified) {
            unifiedSchema.asCatalogAdditionalSchema();
        }
        return unifiedSchema;
    }

    public static UnifiedSchema createAsDynamicSchema(String schemaExpression, DfDatabaseProperties databaseProp) {
        return new UnifiedSchema(schemaExpression).judgeSchema(databaseProp);
    }

    public static UnifiedSchema createAsDynamicSchema(String catalog, String schema, DfDatabaseProperties databaseProp) {
        return new UnifiedSchema(catalog, schema).judgeSchema(databaseProp);
    }

    protected UnifiedSchema asMainSchema() {
        _mainSchema = true;
        _additionalSchema = false;
        _unknownSchema = false;
        _catalogAdditionalSchema = false;
        return this;
    }

    protected UnifiedSchema asCatalogAdditionalSchema() {
        _mainSchema = false;
        _additionalSchema = true;
        _unknownSchema = false;
        _catalogAdditionalSchema = true;
        return this;
    }

    protected UnifiedSchema judgeSchema(DfDatabaseProperties databaseProp) {
        final UnifiedSchema mainSchema = databaseProp.getDatabaseSchema();
        if (equals(mainSchema)) {
            asMainSchema();
        } else {
            final DfAdditionalSchemaInfo info = databaseProp.getAdditionalSchemaInfo(this);
            if (info != null && info.getUnifiedSchema().isCatalogAdditionalSchema()) {
                asCatalogAdditionalSchema();
            } else {
                _unknownSchema = true;
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
            if (sb.length() > 0) {
                sb.append(".");
            }
            if (!NO_NAME_SCHEMA.equals(_schema)) {
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

    public String getPureCatalog() {
        return _catalog;
    }

    public String getPureSchema() {
        if (Srl.is_NotNull_and_NotTrimmedEmpty(_schema) && NO_NAME_SCHEMA.equals(_schema)) {
            return null;
        }
        return _schema;
    }

    // ===================================================================================
    //                                                                 Unique Element Name
    //                                                                 ===================
    public String buildSqlElement(String elementName) {
        if (_mainSchema) {
            return elementName;
        }
        if (_catalogAdditionalSchema) {
            return buildCatalogSchemaElement(elementName);
        } else { // schema-only additional schema
            return buildPureSchemaElement(elementName);
        }
    }

    public String buildCatalogSchemaElement(String elementName) {
        return Srl.connectPrefix(elementName, getCatalogSchema(), ".");
    }

    public String buildIdentifiedSchemaElement(String elementName) {
        return Srl.connectPrefix(elementName, getIdentifiedSchema(), ".");
    }

    public String buildPureSchemaElement(String elementName) {
        return Srl.connectPrefix(elementName, getPureSchema(), ".");
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
        return mySchema != null && mySchema.equals(yourSchema);
    }

    @Override
    public int hashCode() {
        final String identifiedSchema = getIdentifiedSchema();
        return identifiedSchema != null ? identifiedSchema.hashCode() : 17;
    }

    @Override
    public String toString() {
        return DfTypeUtil.toClassTitle(this) + ":{" + getIdentifiedSchema() + "}";
    }
}
