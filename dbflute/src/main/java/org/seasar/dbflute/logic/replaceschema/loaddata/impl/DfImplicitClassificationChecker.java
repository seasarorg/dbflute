/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.logic.replaceschema.loaddata.impl;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.exception.DfLoadDataIllegalImplicitClassificationValueException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.properties.DfClassificationProperties;

/**
 * @author jflute
 * @since 1.0.4G (2013/07/19 Friday)
 */
public class DfImplicitClassificationChecker {

    private static final Log _log = LogFactory.getLog(DfImplicitClassificationChecker.class);

    public void check(File file, String tableDbName, String columnDbName, Connection conn) throws SQLException {
        final DfClassificationProperties prop = getClassificationProperties();
        final String classificationName = prop.getClassificationName(tableDbName, columnDbName);
        if (!prop.isCheckImplicitSet(classificationName)) {
            return;
        }
        final boolean quote = prop.isCodeTypeNeedsQuoted(classificationName);
        final List<String> codeList = prop.getClassificationElementCodeList(classificationName);
        final String sql = buildDistinctSql(tableDbName, columnDbName, quote, codeList);
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            _log.info(sql);
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            final List<String> illegalCodeList = new ArrayList<String>();
            while (rs.next()) {
                illegalCodeList.add(rs.getString(1));
            }
            if (!illegalCodeList.isEmpty()) {
                throwLoadDataIllegalImplicitClassificationValueException(file, tableDbName, columnDbName,
                        classificationName, codeList, illegalCodeList);
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
    }

    protected String buildDistinctSql(String tableDbName, String columnDbName, boolean quote, List<String> codeList) {
        final StringBuilder sb = new StringBuilder();
        sb.append("select distinct " + columnDbName + " from ").append(tableDbName);
        sb.append(" where ").append(columnDbName).append(" not in ");
        sb.append("(");
        int index = 0;
        for (String code : codeList) {
            if (index > 0) {
                sb.append(", ");
            }
            sb.append(quote ? "'" : "").append(code).append(quote ? "'" : "");
            ++index;
        }
        sb.append(")");
        return sb.toString();
    }

    protected void throwLoadDataIllegalImplicitClassificationValueException(File file, String tableDbName,
            String columnDbName, String classificationName, List<String> codeList, List<String> illegalCodeList) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The illegal code of the implicit classification was found.");
        br.addItem("Data File");
        br.addElement(file.getPath());
        br.addItem("Table");
        br.addElement(tableDbName);
        br.addItem("Column");
        br.addElement(columnDbName);
        br.addItem("Classification");
        br.addElement(classificationName);
        br.addItem("Defined Element");
        br.addElement(codeList);
        br.addItem("Illegal Value");
        br.addElement(illegalCodeList);
        final String msg = br.buildExceptionMessage();
        throw new DfLoadDataIllegalImplicitClassificationValueException(msg);
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    protected DfClassificationProperties getClassificationProperties() {
        return getProperties().getClassificationProperties();
    }
}
