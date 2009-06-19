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
 * The handler of SQL result. <br />
 * This handler is called back after executing the SQL and mapping entities. <br />
 * (before you get the result)
 * <pre>
 * context.setSqlResultHandler(new SqlResultHandler() {
 *     public void handle(Object result, String displaySql
 *                      , long before, long after) {
 *         // You can get your SQL result object here.
 *     }
 * });
 * </pre>
 * <p>
 * Attention: <br />
 * If the SQL would be not executed, this is not called back.
 * For example, update() that the entity has no modification. <br />
 * And though if the command would be for batch, this is called back only once in a command.
 * So The displaySql is the latest SQL in a command at that time.
 * </p>
 * @author jflute
 */
public interface SqlResultHandler {

    /**
     * Handle the SQL result.
     * @param result The result of executed SQL. (Nullable)
     * @param displaySql The SQL for display. This is the latest SQL in a command. (NotNull)
     * @param before The time in millisecond before executing command(immediate after initializing executions).
     * @param after The time in millisecond after executing command(immediate after mapping entities).
     */
    void handle(Object result, String displaySql, long before, long after);
}
