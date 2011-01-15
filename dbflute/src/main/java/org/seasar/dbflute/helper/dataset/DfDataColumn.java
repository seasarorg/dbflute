package org.seasar.dbflute.helper.dataset;

import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.dataset.types.DfDtsColumnType;
import org.seasar.dbflute.properties.DfLittleAdjustmentProperties;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DfDataColumn {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private final String _columnDbName;
    private DfDtsColumnType _columnType; // can be overridden
    private int _columnIndex; // can be overridden
    private boolean _primaryKey = false;
    private boolean _writable = true;
    private String _formatPattern;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfDataColumn(String columnDbName, DfDtsColumnType columnType, int columnIndex) {
        this._columnDbName = columnDbName;
        this._columnType = columnType;
        this._columnIndex = columnIndex;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getColumnDbName() {
        return _columnDbName;
    }

    public String getColumnSqlName() {
        return quoteColumnNameIfNeeds(_columnDbName);
    }

    protected String quoteColumnNameIfNeeds(String columnDbName) {
        final DfLittleAdjustmentProperties prop = DfBuildProperties.getInstance().getLittleAdjustmentProperties();
        return prop.quoteColumnNameIfNeedsDirectUse(columnDbName);
    }

    public DfDtsColumnType getColumnType() {
        return _columnType;
    }

    public void setColumnType(DfDtsColumnType columnType) {
        this._columnType = columnType;
    }

    public int getColumnIndex() {
        return _columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this._columnIndex = columnIndex;
    }

    public boolean isPrimaryKey() {
        return _primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this._primaryKey = primaryKey;
    }

    public boolean isWritable() {
        return _writable;
    }

    public void setWritable(boolean writable) {
        this._writable = writable;
    }

    public String getFormatPattern() {
        return _formatPattern;
    }

    public void setFormatPattern(String formatPattern) {
        this._formatPattern = formatPattern;
    }

    public Object convert(Object value) {
        return _columnType.convert(value, _formatPattern);
    }
}
