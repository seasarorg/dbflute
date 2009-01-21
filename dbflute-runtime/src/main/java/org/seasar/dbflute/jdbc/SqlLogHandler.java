/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.jdbc;

/**
 * The handler of SQL log.
 * <pre>
 * context.setSqlLogHandler(new SqlLogHandler() {
 *     public void handle(String executedSql, String displaySql
 *                      , Object[] args, Class&lt;?&gt;[] argTypes) {
 *         // You can get your SQL string here.
 *     }
 * });
 * </pre>
 * @author jflute
 */
public interface SqlLogHandler {

    /**
     * Handle the SQL log.
     * @param executedSql The executed SQL. (NotNull)
     * @param displaySql The SQL for display. (NotNull)
     * @param args The arguments of the SQL. (Nullable)
     * @param argTypes The argument types of the SQL. (Nullable)
     */
    void handle(String executedSql, String displaySql, Object[] args, Class<?>[] argTypes);
}
