package org.apache.torque.engine.database.model;

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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.torque.engine.EngineException;
import org.xml.sax.Attributes;

/**
 * Information about indices of a table.
 * @author Modified by jflute
 */
public class Index {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final Integer FIRST_POSITION = Integer.valueOf(1);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The name of the index. */
    private String _indexName;

    /** The table. */
    private Table _table;

    /** The map of index columns. {ordinalPosition : columnName} */
    private final Map<Integer, String> _indexColumnMap = new LinkedHashMap<Integer, String>();

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    /**
     * Creates a name for the index using the NameFactory.
     * @throws EngineException if the name could not be created
     */
    private void createName() throws EngineException { // Unused on DBFlute
        final Table table = getTable();
        final List<Object> inputs = new ArrayList<Object>(4);
        inputs.add(table.getDatabase());
        inputs.add(table.getName());
        if (isUnique()) {
            inputs.add("U");
        } else {
            inputs.add("I");
        }
        // ASSUMPTION: This Index not yet added to the list.
        inputs.add(new Integer(table.getIndices().length + 1));
        _indexName = NameFactory.generateName(NameFactory.CONSTRAINT_GENERATOR, inputs);
    }

    // ===================================================================================
    //                                                                         XML Loading
    //                                                                         ===========
    /**
     * Imports index from an XML specification
     * @param attrib the xml attributes
     */
    public void loadFromXML(Attributes attrib) {
        _indexName = attrib.getValue("name");
    }

    /**
     * Adds a new column to an index.
     * @param attrib xml attributes for the column
     */
    public void addColumn(Attributes attrib) {
        final String columnName = attrib.getValue("name");
        final Integer ordinalPosition;
        {
            final String ordinalPositionString = attrib.getValue("position");
            ordinalPosition = Integer.parseInt(ordinalPositionString);
        }
        _indexColumnMap.put(ordinalPosition, columnName);
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean hasSameColumnSet(List<Column> columnList) {
        for (final Column column : columnList) {
            if (!getIndexColumnMap().containsValue(column.getName())) {
                return false;
            }
        }
        if (getIndexColumnMap().size() != columnList.size()) {
            return false;
        }
        return true;
    }

    public boolean hasSameFirstColumn(Column column) {
        final String first = getIndexColumnMap().get(FIRST_POSITION);
        if (first == null) {
            return false;
        }
        return first.equalsIgnoreCase(column.getName());
    }

    public boolean hasSameColumn(Column column) {
        final Map<Integer, String> indexColumnMap = getIndexColumnMap();
        final Set<Entry<Integer, String>> entrySet = indexColumnMap.entrySet();
        for (Entry<Integer, String> entry : entrySet) {
            final String columnName = entry.getValue();
            if (column.getName().equalsIgnoreCase(columnName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the uniqueness of this index.
     * @return the uniqueness of this index
     */
    public boolean isUnique() {
        return false;
    }

    public boolean isOnlyOneColumn() {
        return _indexColumnMap.size() == 1;
    }

    public boolean isTwoOrMoreColumn() {
        return _indexColumnMap.size() > 1;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    /**
     * String representation of the index. This is an xml representation.
     * @return a xml representation
     */
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(" <index name=\"").append(getName()).append("\"");

        result.append(">\n");

        final Set<Integer> keySet = _indexColumnMap.keySet();
        for (Integer position : keySet) {
            final String columnName = _indexColumnMap.get(position);
            result.append("  <index-column name=\"").append(columnName).append("\" position=\"" + position + "\"/>\n");
        }
        result.append(" </index>\n");
        return result.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * Gets the name of this index.
     * @return the name of this index
     */
    public String getName() {
        if (_indexName == null) {
            try {
                // generate an index name if we don't have a supplied one
                createName();
            } catch (EngineException e) {
                // still no name
            }
        }
        return _indexName;
    }

    /**
     * Set the name of this index.
     * @param indexName the name of this index
     */
    public void setName(String indexName) {
        this._indexName = indexName;
    }

    /**
     * Get the parent Table of the index
     * @return the table
     */
    public Table getTable() {
        return _table;
    }

    /**
     * Set the parent Table of the index
     * @param parent the table
     */
    public void setTable(Table parent) {
        _table = parent;
    }

    public Map<Integer, String> getIndexColumnMap() {
        return _indexColumnMap;
    }

    public void addColumn(String columnName) {
        Integer position;
        if (_indexColumnMap.isEmpty()) {
            position = FIRST_POSITION;
        } else {
            position = Integer.valueOf(_indexColumnMap.size() + 1);
        }
        _indexColumnMap.put(position, columnName);
    }
}
