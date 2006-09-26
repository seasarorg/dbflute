package org.apache.torque.engine.database.transform;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.torque.engine.database.model.AppData;
import org.apache.torque.engine.database.model.Column;
import org.apache.torque.engine.database.model.Database;
import org.apache.torque.engine.database.model.ForeignKey;
import org.apache.torque.engine.database.model.IDMethod;
import org.apache.torque.engine.database.model.Table;
import org.apache.torque.engine.sql.ParseException;
import org.apache.torque.engine.sql.SQLScanner;
import org.apache.torque.engine.sql.Token;

/**
 * A Class that converts an sql input file to an AppData
 * structure.  The class makes use of SQL Scanner to get
 * sql tokens and the parses these to create the AppData
 * class. SQLToAppData is in effect a simplified sql parser.
 *
 * @author <a href="mailto:leon@opticode.co.za">Leon Messerschmidt</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @version $Id$
 */
public class SQLToAppData
{
    private String sqlFile;
    private List tokens;
    private Token token;
    private AppData appData;
    private Database appDataDB;
    private int count;
    private String databaseType;
    private String basePropsFilePath;

    /**
     * Create a new class with an input Reader
     *
     * @param sqlFile the sql file
     */
    public SQLToAppData(String sqlFile)
    {
        this.sqlFile = sqlFile;
    }

    /**
     * Create a new class with an input Reader.  This ctor is not used
     * but putting here in the event db.props properties are found to
     * be useful converting sql to xml, the infrastructure will exist
     *
     * @param sqlFile the sql file
     * @param databaseType
     * @param basePropsFilePath
     */
    public SQLToAppData(String sqlFile, String databaseType,
                        String basePropsFilePath)
    {
        this.sqlFile = sqlFile;
        this.databaseType = databaseType;
        this.basePropsFilePath = basePropsFilePath;
    }

    /**
     * Get the current input sql file
     *
     * @return the sql file
     */
    public String getSqlFile()
    {
        return sqlFile;
    }

    /**
     * Set the current input sql file
     *
     * @param sqlFile the sql file
     */
    public void setSqlFile(String sqlFile)
    {
        this.sqlFile = sqlFile;
    }

    /**
     * Move to the next token.  Throws an exception
     * if there is no more tokens available.
     *
     * @throws ParseException
     */
    private void next() throws ParseException
    {
        if (count < tokens.size())
        {
            token = (Token) tokens.get(count++);
        }
        else
        {
            throw new ParseException("No More Tokens");
        }
    }

    /**
     * Creates an error condition and adds the line and
     * column number of the current token to the error
     * message.
     *
     * @param name name of the error
     * @throws ParseException
     */
    private void err(String name) throws ParseException
    {
        throw new ParseException (name + " at [ line: " + token.getLine()
                + " col: " + token.getCol() + " ]");
    }

    /**
     * Check if there is more tokens available for parsing.
     *
     * @return true if there are more tokens available
     */
    private boolean hasTokens()
    {
        return count < tokens.size();
    }

    /**
     * Parses a CREATE TABLE FOO command.
     *
     * @throws ParseException
     */
    private void create() throws ParseException
    {
        next();
        if (token.getStr().toUpperCase().equals("TABLE"))
        {
            create_Table();
        }
    }

    /**
     * Parses a CREATE TABLE sql command
     *
     * @throws ParseException error parsing the input file
     */
    private void create_Table() throws ParseException
    {
        next();
        String tableName = token.getStr(); // name of the table
        next();
        if (!token.getStr().equals("("))
        {
            err("( expected");
        }
        next();

        Table tbl = new Table (tableName);
        //tbl.setIdMethod("none");
        while (!token.getStr().equals(";"))
        {
            create_Table_Column(tbl);
        }

        if (tbl.getPrimaryKey().size() == 1)
        {
            tbl.setIdMethod(IDMethod.ID_BROKER);
        }
        else
        {
            tbl.setIdMethod(IDMethod.NO_ID_METHOD);
        }
        appDataDB.addTable (tbl);
    }

    /**
     * Parses column information between the braces of a CREATE
     * TABLE () sql statement.
     *
     * @throws ParseException error parsing the input file
     */
    private void create_Table_Column(Table tbl) throws ParseException
    {
        // The token should be the first item
        // which is the name of the column or
        // PRIMARY/FOREIGN/UNIQUE
        if (token.getStr().equals(","))
        {
            next();
        }

        if (token.getStr().toUpperCase().equals("PRIMARY"))
        {
            create_Table_Column_Primary(tbl);
        }
        else if (token.getStr().toUpperCase().equals("FOREIGN"))
        {
            create_Table_Column_Foreign(tbl);
        }
        else if (token.getStr().toUpperCase().equals("UNIQUE"))
        {
            create_Table_Column_Unique(tbl);
        }
        else
        {
            create_Table_Column_Data(tbl);
        }
    }

    /**
     * Parses PRIMARY KEY (FOO,BAR) statement
     *
     * @throws ParseException error parsing the input file
     */
    private void create_Table_Column_Primary (Table tbl) throws ParseException
    {
        next();
        if (!token.getStr().toUpperCase().equals("KEY"))
        {
            err("KEY expected");
        }
        next();
        if (!token.getStr().toUpperCase().equals("("))
        {
            err("( expected");
        }
        next();

        String colName = token.getStr();
        Column c = tbl.getColumn(colName);
        if (c == null)
        {
            err("Invalid column name: " + colName);
        }
        c.setPrimaryKey(true);
        next();
        while (token.getStr().equals(","))
        {
            next();
            colName = token.getStr();
            c = tbl.getColumn(colName);
            if (c == null)
            {
                err("Invalid column name: " + colName);
            }
            c.setPrimaryKey(true);
            next();
        }

        if (!token.getStr().toUpperCase().equals(")"))
        {
            err(") expected");
        }
        next(); // skip the )
    }

    /**
     * Parses UNIQUE (NAME,FOO,BAR) statement
     *
     * @throws ParseException error parsing the input file
     */
    private void create_Table_Column_Unique(Table tbl) throws ParseException
    {
        next();
        if (!token.getStr().toUpperCase().equals("("))
        {
            err("( expected");
        }
        next();
        while (!token.getStr().equals(")"))
        {
            if (!token.getStr().equals(","))
            {
                String colName = token.getStr();
                Column c = tbl.getColumn(colName);
                if (c == null)
                {
                    err("Invalid column name: " + colName);
                }
                c.setUnique(true);
            }
            next();
        }
        if (!token.getStr().toUpperCase().equals(")"))
        {
            err(") expected got: " + token.getStr());
        }

        next(); // skip the )
    }

    /**
     * Parses FOREIGN KEY (BAR) REFERENCES TABLE (BAR) statement
     *
     * @throws ParseException error parsing the input file
     */
    private void create_Table_Column_Foreign(Table tbl) throws ParseException
    {
        next();
        if (!token.getStr().toUpperCase().equals("KEY"))
        {
            err("KEY expected");
        }
        next();
        if (!token.getStr().toUpperCase().equals("("))
        {
            err("( expected");
        }
        next();

        final ForeignKey fk = new ForeignKey();
        final List<String> localColumns = new ArrayList<String>();
        tbl.addForeignKey(fk);

        String colName = token.getStr();
        localColumns.add(colName);
        next();
        while (token.getStr().equals(","))
        {
            next();
            colName = token.getStr();
            localColumns.add(colName);
            next();
        }
        if (!token.getStr().toUpperCase().equals(")"))
        {
            err(") expected");
        }

        next();

        if (!token.getStr().toUpperCase().equals("REFERENCES"))
        {
            err("REFERENCES expected");
        }

        next();

        fk.setForeignTableName(token.getStr());

        next();

        if (token.getStr().toUpperCase().equals("("))
        {
            next();
            int i = 0;
            fk.addReference((String) localColumns.get(i++), token.getStr());
            next();
            while (token.getStr().equals(","))
            {
                next();
                fk.addReference((String) localColumns.get(i++), token.getStr());
                next();
            }
            if (!token.getStr().toUpperCase().equals(")"))
            {
                err(") expected");
            }
            next();
        }
    }

    /**
     * Parse the data definition of the column statement.
     *
     * @throws ParseException error parsing the input file
     */
    private void create_Table_Column_Data(Table tbl) throws ParseException
    {
        String columnSize = null;
        String columnPrecision = null;
        String columnDefault = null;
        boolean inEnum = false;

        String columnName = token.getStr();
        next();
        String columnType = token.getStr();

        if (columnName.equals(")") && columnType.equals(";"))
        {
            return;
        }

        next();

        // special case for MySQL ENUM's which are stupid anyway
        // and not properly handled by Torque.
        if (columnType.toUpperCase().equals("ENUM"))
        {
            inEnum = true;
            next(); // skip (
            while (!token.getStr().equals(")"))
            {
                // skip until )
                next();
            }
            while (!token.getStr().equals(","))
            {
                if (token.getStr().toUpperCase().equals("DEFAULT"))
                {
                    next();
                    if (token.getStr().equals("'"))
                    {
                        next();
                    }
                    columnDefault = token.getStr();
                    next();
                    if (token.getStr().equals("'"))
                    {
                        next();
                    }
                }
                // skip until ,
                next();
            }
            next(); // skip ,
            columnType = "VARCHAR";
        }
        else if (token.getStr().toUpperCase().equals("("))
        {
            next();
            columnSize = token.getStr();
            next();
            if (token.getStr().equals(","))
            {
                next();
                columnPrecision = token.getStr();
                next();
            }

            if (!token.getStr().equals(")"))
            {
                err(") expected");
            }
            next();
        }

        Column col = new Column(columnName);
        if (columnPrecision != null)
        {
            columnSize = columnSize + columnPrecision;
        }
        col.setTypeFromString(columnType, columnSize);
        tbl.addColumn(col);

        if (inEnum)
        {
            col.setNotNull(true);
            if (columnDefault != null)
            {
                col.setDefaultValue(columnDefault);
            }
        }
        else
        {
            while (!token.getStr().equals(",") && !token.getStr().equals(")"))
            {
                if (token.getStr().toUpperCase().equals("NOT"))
                {
                    next();
                    if (!token.getStr().toUpperCase().equals("NULL"))
                    {
                        err("NULL expected after NOT");
                    }
                    col.setNotNull(true);
                    next();
                }
                else if (token.getStr().toUpperCase().equals("PRIMARY"))
                {
                    next();
                    if (!token.getStr().toUpperCase().equals("KEY"))
                    {
                        err("KEY expected after PRIMARY");
                    }
                    col.setPrimaryKey(true);
                    next();
                }
                else if (token.getStr().toUpperCase().equals("UNIQUE"))
                {
                    col.setUnique(true);
                    next();
                }
                else if (token.getStr().toUpperCase().equals("NULL"))
                {
                    col.setNotNull(false);
                    next();
                }
                else if (token.getStr().toUpperCase().equals("AUTO_INCREMENT"))
                {
                    col.setAutoIncrement(true);
                    next();
                }
                else if (token.getStr().toUpperCase().equals("DEFAULT"))
                {
                    next();
                    if (token.getStr().equals("'"))
                    {
                        next();
                    }
                    col.setDefaultValue(token.getStr());
                    next();
                    if (token.getStr().equals("'"))
                    {
                        next();
                    }
                }
            }
            next(); // eat the ,
        }
    }

    /**
     * Execute the parser.
     *
     * @throws IOException If an I/O error occurs
     * @throws ParseException error parsing the input file
     */
    public AppData execute() throws IOException, ParseException
    {
        count = 0;
        appData = new AppData(databaseType, basePropsFilePath);
        appDataDB = new Database();
        appData.addDatabase(appDataDB);

        FileReader fr = new FileReader(sqlFile);
        BufferedReader br = new BufferedReader(fr);
        SQLScanner scanner = new SQLScanner(br);

        tokens = scanner.scan();

        br.close();

        while (hasTokens())
        {
            if (token == null)
            {
                next();
            }

            if (token.getStr().toUpperCase().equals("CREATE"))
            {
                create();
            }
            if (hasTokens())
            {
                next();
            }
        }
        return appData;
    }

    /**
     * Just 4 testing.
     *
     * @param args commandline args
     * @throws Exception an exception
     */
    public static void main(String args[]) throws Exception
    {
        SQLToAppData s2a = new SQLToAppData(args[0]);
        AppData ad = s2a.execute();
        System.out.println(ad);
    }
}
