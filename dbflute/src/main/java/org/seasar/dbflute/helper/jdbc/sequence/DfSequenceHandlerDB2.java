/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.helper.jdbc.sequence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

/**
 * @author jflute
 * @since 0.9.5.2 (2009/07/09 Thursday)
 */
public class DfSequenceHandlerDB2 extends DfSequenceHandlerJdbc {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfSequenceHandlerDB2(DataSource dataSource, String schema) {
        super(dataSource, schema);
    }

    // ===================================================================================
    //                                                                          Next Value
    //                                                                          ==========
    @Override
    protected Integer selectNextVal(Statement statement, String sequenceName) throws SQLException {
        ResultSet rs = statement.executeQuery("values nextval for " + sequenceName);
        rs.next();
        return rs.getInt(1);
    }
}