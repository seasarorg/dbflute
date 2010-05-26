package org.seasar.dbflute.logic.dftask.sql2entity.pmbean;

import java.util.List;

import org.apache.torque.engine.EngineException;
import org.apache.torque.engine.database.model.AppData;
import org.apache.torque.engine.database.model.Column;
import org.apache.torque.engine.database.model.Database;
import org.apache.torque.engine.database.model.Table;
import org.seasar.dbflute.exception.DfParameterBeanReferenceColumnNotFoundException;
import org.seasar.dbflute.exception.DfParameterBeanReferenceTableNotFoundException;
import org.seasar.dbflute.util.DfSystemUtil;

/**
 * @author jflute
 * @since 0.9.6.1 (2009/11/17 Tuesday)
 */
public class PmbMetaDataPropertyOptionReference {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final String OPTION_PREFIX = "ref(";
    protected static final String OPTION_SUFFIX = ")";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _className;
    protected String _propertyName;
    protected PmbMetaDataPropertyOptionFinder _pmbMetaDataPropertyOptionFinder;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public PmbMetaDataPropertyOptionReference(String className, String propertyName,
            PmbMetaDataPropertyOptionFinder pmbMetaDataPropertyOptionFinder) {
        _className = className;
        _propertyName = propertyName;
        _pmbMetaDataPropertyOptionFinder = pmbMetaDataPropertyOptionFinder;
    }

    // ===================================================================================
    //                                                                    Reference Column
    //                                                                    ================
    public Column getPmbMetaDataPropertyOptionReferenceColumn(AppData appData) {
        if (appData == null) {
            return null;
        }
        final Database database;
        try {
            database = appData.getDatabase();
        } catch (EngineException e) {
            throw new IllegalStateException(e);
        }
        if (database == null) {
            return null;
        }
        final String refPrefix = "ref(";
        final String refSuffix = ")";
        String option = getPmbMetaDataPropertyOption();
        {
            if (option == null) {
                return null;
            }
            final List<String> splitOption = splitOption(option);
            String firstOption = null;
            for (String element : splitOption) {
                element = element.trim();
                if (element.startsWith(refPrefix) && element.endsWith(refSuffix)) {
                    firstOption = element;
                    break;
                }
            }
            if (firstOption == null) {
                return null;
            }
            option = firstOption;
        }
        final int clsIdx = refPrefix.length();
        final int clsEndIdx = option.length() - refSuffix.length();
        final String value;
        try {
            value = option.substring(refPrefix.length(), option.length() - refSuffix.length()).trim();
        } catch (StringIndexOutOfBoundsException e) {
            String msg = "Look the message below:" + ln();
            msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * " + ln();
            msg = msg + "IndexOutOfBounds ocurred:" + ln();
            msg = msg + " " + _className + " " + _propertyName;
            msg = msg + ":" + option + ln();
            msg = msg + "{" + option + "}.substring(" + clsIdx + ", " + clsEndIdx + ")" + ln();
            msg = msg + "* * * * * * * * * */";
            throw new IllegalStateException(msg, e);
        }
        final int delimiterIndex = value.indexOf(".");
        final String tableName;
        final String columnName;
        if (delimiterIndex < 0) {
            tableName = value;
            columnName = null;
        } else {
            tableName = value.substring(0, delimiterIndex);
            columnName = value.substring(delimiterIndex + ".".length());
        }
        final Table table = database.getTable(tableName);
        if (table == null) {
            throwParameterBeanReferenceTableNotFoundException(_className, _propertyName, tableName);
        }
        final Column column;
        if (columnName != null) {
            column = table.getColumn(columnName);
        } else {
            column = table.getColumn(_propertyName);
        }
        if (column == null) {
            throwParameterBeanReferenceColumnNotFoundException(_className, _propertyName, tableName, columnName);
        }
        return column;
    }

    protected void throwParameterBeanReferenceTableNotFoundException(String className, String propertyName,
            String tableName) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The reference table was not found!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm the table existence." + ln();
        msg = msg + ln();
        msg = msg + "[ParameterBean]" + ln() + className + ln();
        msg = msg + ln();
        msg = msg + "[Property]" + ln() + propertyName + ln();
        msg = msg + ln();
        msg = msg + "[Not Found Table]" + ln() + tableName + ln();
        msg = msg + "* * * * * * * * * */";
        throw new DfParameterBeanReferenceTableNotFoundException(msg);
    }

    protected void throwParameterBeanReferenceColumnNotFoundException(String className, String propertyName,
            String tableName, String columnName) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The reference column was not found!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm the column existence." + ln();
        msg = msg + ln();
        msg = msg + "[ParameterBean]" + ln() + className + ln();
        msg = msg + ln();
        msg = msg + "[Property]" + ln() + propertyName + ln();
        msg = msg + ln();
        msg = msg + "[Table]" + ln() + tableName + ln();
        msg = msg + ln();
        msg = msg + "[Column]" + ln() + columnName + ln();
        msg = msg + "* * * * * * * * * */";
        throw new DfParameterBeanReferenceColumnNotFoundException(msg);
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected String getPmbMetaDataPropertyOption() {
        return _pmbMetaDataPropertyOptionFinder.findPmbMetaDataPropertyOption(_className, _propertyName);
    }

    protected List<String> splitOption(String option) {
        return PmbMetaDataPropertyOptionFinder.splitOption(option);
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }
}
