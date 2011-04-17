package org.apache.torque.task;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.apache.torque.engine.database.transform.XmlToAppData.XmlReadingTableFilter;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.logic.sql2entity.analyzer.DfOutsideSqlPack;
import org.seasar.dbflute.logic.sql2entity.bqp.DfBehaviorQueryPathSetupper;
import org.seasar.dbflute.properties.DfDatabaseProperties;
import org.seasar.dbflute.properties.DfLittleAdjustmentProperties;
import org.seasar.dbflute.properties.assistant.DfAdditionalSchemaInfo;
import org.seasar.dbflute.task.bs.DfAbstractDbMetaTexenTask;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.DfNameHintUtil;

/**
 * The DBFlute task generating classes from schema meta data.
 * @author Modified by jflute
 */
public class TorqueDataModelTask extends DfAbstractDbMetaTexenTask {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(TorqueDataModelTask.class);

    // ===================================================================================
    //                                                                         Data Source
    //                                                                         ===========
    @Override
    protected boolean isUseDataSource() {
        return true;
    }

    // ===================================================================================
    //                                                                        Table Filter
    //                                                                        ============
    @Override
    protected XmlReadingTableFilter createXmlReadingTableFilter() {
        return new GenetateXmlReadingTableFilter(getDatabaseProperties());
    }

    public static class GenetateXmlReadingTableFilter implements XmlReadingTableFilter {
        protected DfDatabaseProperties _databaseProp;

        public GenetateXmlReadingTableFilter(DfDatabaseProperties databaseProp) {
            _databaseProp = databaseProp;
        }

        public boolean isExcept(UnifiedSchema unifiedSchema, String tableName) {
            final DfAdditionalSchemaInfo additional = _databaseProp.getAdditionalSchemaInfo(unifiedSchema);
            final List<String> tableExceptGenOnlyList;
            if (additional != null) {
                tableExceptGenOnlyList = additional.getTableExceptGenOnlyList();
            } else {
                tableExceptGenOnlyList = _databaseProp.getTableExceptGenOnlyList();
            }
            final List<String> targetEmptyList = DfCollectionUtil.emptyList();
            return !isTargetByHint(tableName, targetEmptyList, tableExceptGenOnlyList);
        }

        protected boolean isTargetByHint(String name, List<String> targetList, List<String> exceptList) {
            return DfNameHintUtil.isTargetByHint(name, targetList, exceptList);
        }
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Override
    protected void doExecute() {
        setupControlTemplate();
        super.doExecute();
        setupBehaviorQueryPath();
        showSkippedFileInformation();
        refreshResources();
    }

    protected void setupControlTemplate() {
        final DfLittleAdjustmentProperties littleProp = DfBuildProperties.getInstance().getLittleAdjustmentProperties();
        if (littleProp.isAlternateGenerateControlValid()) {
            setupControlAlternate(littleProp);
            return;
        }
        if (getBasicProperties().isTargetLanguageMain()) {
            if (getBasicProperties().isTargetLanguageJava()) {
                if (getBasicProperties().isApplicationBehaviorProject()) {
                    setupControlApplicationBehaviorJava(littleProp);
                    return;
                }
                _log.info("");
                _log.info("* * * * * * * * *");
                _log.info("* Process Java  *");
                _log.info("* * * * * * * * *");
                final String control = "om/ControlGenerateJava.vm";
                _log.info("...Using Java control: " + control);
                setControlTemplate(control);
            } else if (getBasicProperties().isTargetLanguageCSharp()) {
                if (getBasicProperties().isApplicationBehaviorProject()) {
                    setupControlApplicationBehaviorCSharp(littleProp);
                    return;
                }
                _log.info("");
                _log.info("* * * * * * * * * *");
                _log.info("* Process CSharp  *");
                _log.info("* * * * * * * * * *");
                final String control = "om/ControlGenerateCSharp.vm";
                _log.info("...Using CSharp control: " + control);
                setControlTemplate(control);
            } else {
                String msg = "Unknown main language: " + getBasicProperties().getTargetLanguage();
                throw new IllegalStateException(msg);
            }
        } else {
            setupControlSubLanguage();
        }
    }

    protected void setupControlAlternate(DfLittleAdjustmentProperties littleProp) {
        _log.info("");
        _log.info("* * * * * * * * * * * * * * *");
        _log.info("* Process Alternate Control *");
        _log.info("* * * * * * * * * * * * * * *");
        final String control = littleProp.getAlternateGenerateControl();
        _log.info("...Using alternate control: " + control);
        setControlTemplate(control);
    }

    protected void setupControlApplicationBehaviorJava(DfLittleAdjustmentProperties littleProp) {
        _log.info("");
        _log.info("* * * * * * * * * * * * * * * * * * * * * * * *");
        _log.info("* Process Application Behavior Control (Java) *");
        _log.info("* * * * * * * * * * * * * * * * * * * * * * * *");
        final String control = "om/java/plugin/bhvap/ControlBhvApJava.vm";
        _log.info("...Using application behavior (Java) control: " + control);
        setControlTemplate(control);
    }

    protected void setupControlApplicationBehaviorCSharp(DfLittleAdjustmentProperties littleProp) {
        _log.info("");
        _log.info("* * * * * * * * * * * * * * * * * * * * * * * * *");
        _log.info("* Process Application Behavior Control (CSharp) *");
        _log.info("* * * * * * * * * * * * * * * * * * * * * * * * *");
        final String control = "om/csharp/plugin/bhvap/ControlBhvApCSharp.vm";
        _log.info("...Using application behavior (CSharp) control: " + control);
        setControlTemplate(control);
    }

    protected void setupControlSubLanguage() {
        final String language = getBasicProperties().getTargetLanguage();
        _log.info("");
        _log.info("* * * * * * * * * *");
        _log.info("* Process " + language + "    *");
        _log.info("* * * * * * * * * *");
        final String control = "om/" + language + "/Control-" + language + ".vm";
        _log.info("...Using " + language + " control: " + control);
        setControlTemplate(control);
    }

    // ===================================================================================
    //                                                                 Behavior Query Path
    //                                                                 ===================
    protected void setupBehaviorQueryPath() {
        final DfOutsideSqlPack outsideSqlPack = collectOutsideSql();
        final DfBehaviorQueryPathSetupper setupper = new DfBehaviorQueryPathSetupper();
        setupper.setupBehaviorQueryPath(outsideSqlPack);
    }
}