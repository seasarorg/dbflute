/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.logic.sql2entity.pmbean;

import java.util.List;

import org.apache.torque.engine.EngineException;
import org.apache.torque.engine.database.model.AppData;
import org.apache.torque.engine.database.model.Column;
import org.apache.torque.engine.database.model.Database;
import org.apache.torque.engine.database.model.Table;
import org.seasar.dbflute.exception.DfParameterBeanReferenceColumnNotFoundException;
import org.seasar.dbflute.exception.DfParameterBeanReferenceTableNotFoundException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.util.DfSystemUtil;
import org.seasar.dbflute.util.Srl;
import org.seasar.dbflute.util.Srl.ScopeInfo;

/**
 * @author jflute
 * @since 0.9.6.1 (2009/11/17 Tuesday)
 */
public class DfPmbPropertyOptionReference {

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
    protected DfPmbPropertyOptionFinder _pmbMetaDataPropertyOptionFinder;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfPmbPropertyOptionReference(String className, String propertyName,
            DfPmbPropertyOptionFinder pmbMetaDataPropertyOptionFinder) {
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
        final String refPrefix = OPTION_PREFIX;
        final String refSuffix = OPTION_SUFFIX;
        final String option;
        {
            final String optionExp = getPmbMetaDataPropertyOption();
            if (optionExp == null) {
                return null;
            }
            final List<String> splitOption = splitOption(optionExp);
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
        final ScopeInfo scope = Srl.extractScopeFirst(option, refPrefix, refSuffix);
        final String content = scope.getContent().trim();
        final String delimiter = ".";
        final String tableName;
        final String columnName;
        if (content.contains(".")) {
            tableName = Srl.substringFirstFront(content, delimiter);
            columnName = Srl.substringFirstRear(content, delimiter);
        } else {
            tableName = content;
            columnName = null;
        }
        final Table table = database.getTable(tableName);
        if (table == null) {
            throwParameterBeanReferenceTableNotFoundException(option, _className, _propertyName, tableName);
        }
        final Column column;
        if (columnName != null) {
            column = table.getColumn(columnName);
        } else {
            column = table.getColumn(_propertyName);
        }
        if (column == null) {
            throwParameterBeanReferenceColumnNotFoundException(option, _className, _propertyName, tableName, columnName);
        }
        return column;
    }

    protected void throwParameterBeanReferenceTableNotFoundException(String option, String className,
            String propertyName, String tableName) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The reference table for parameter-bean property was not found!");
        br.addItem("Advice");
        br.addElement("Please confirm the table existence.");
        br.addElement("For example:");
        br.addElement("  (x):");
        br.addElement("    -- !!String memberName:ref(NOT_EXIST.MEMBER_NAME)!!");
        br.addElement("  (o):");
        br.addElement("    -- !!String memberName:ref(MEMBER.MEMBER_NAME)!!");
        br.addItem("ParameterBean");
        br.addElement(className);
        br.addItem("Property");
        br.addElement(propertyName);
        br.addItem("NotFound Table");
        br.addElement(tableName);
        br.addItem("Option");
        br.addElement(option);
        final String msg = br.buildExceptionMessage();
        throw new DfParameterBeanReferenceTableNotFoundException(msg);
    }

    protected void throwParameterBeanReferenceColumnNotFoundException(String option, String className,
            String propertyName, String tableName, String columnName) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The reference column for parameter-bean property was not found!");
        br.addItem("Advice");
        br.addElement("Please confirm the column existence.");
        br.addElement("For example:");
        br.addElement("  (x):");
        br.addElement("    -- !!String memberName:ref(MEMBER.NOT_EXIST_NAME)!!");
        br.addElement("  (o):");
        br.addElement("    -- !!String memberName:ref(MEMBER.MEMBER_NAME)!!");
        br.addItem("ParameterBean");
        br.addElement(className);
        br.addItem("Property");
        br.addElement(propertyName);
        br.addItem("Table");
        br.addElement(tableName);
        br.addItem("NotFound Column");
        br.addElement(columnName);
        br.addItem("Option");
        br.addElement(option);
        final String msg = br.buildExceptionMessage();
        throw new DfParameterBeanReferenceColumnNotFoundException(msg);
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected String getPmbMetaDataPropertyOption() {
        return _pmbMetaDataPropertyOptionFinder.findPmbMetaDataPropertyOption(_className, _propertyName);
    }

    protected List<String> splitOption(String option) {
        return DfPmbPropertyOptionFinder.splitOption(option);
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }
}
