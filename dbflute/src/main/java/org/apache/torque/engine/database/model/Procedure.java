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

import java.math.BigDecimal;

import org.apache.torque.engine.database.transform.XmlToAppData.XmlReadingFilter;
import org.xml.sax.Attributes;

/**
 * @author awaawa
 * @author jflute
 * @since 0.9.9.7F (2012/08/20 Monday)
 */
public class Procedure {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Database _database;
    protected String _name;
    protected UnifiedSchema _unifiedSchema;
    protected BigDecimal _sourceLine;
    protected BigDecimal _sourceSize;
    protected BigDecimal _sourceHash;
    protected String _procedureComment;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    // -----------------------------------------------------
    //                                         Load from XML
    //                                         -------------
    public boolean loadFromXML(Attributes attrib, XmlReadingFilter readingFilter) {
        _name = attrib.getValue("name"); // procedure name
        _unifiedSchema = UnifiedSchema.createAsDynamicSchema(attrib.getValue("schema"));
        if (readingFilter != null && readingFilter.isProcedureExcept(_unifiedSchema, _name)) {
            return false;
        }
        final String sourceLine = attrib.getValue("sourceLine");
        if (sourceLine != null) {
            try {
                _sourceLine = new BigDecimal(sourceLine);
            } catch (NumberFormatException ignored) { // just in case
            }
        }
        final String sourceSize = attrib.getValue("sourceSize");
        if (sourceSize != null) {
            try {
                _sourceSize = new BigDecimal(sourceSize);
            } catch (NumberFormatException ignored) { // just in case
            }
        }
        final String sourceHash = attrib.getValue("sourceHash");
        if (sourceHash != null) {
            try {
                _sourceHash = new BigDecimal(sourceHash);
            } catch (NumberFormatException ignored) { // just in case
            }
        }
        _procedureComment = attrib.getValue("procedureComment");
        return true;
    }

    // ===================================================================================
    //                                                                    Derived Property
    //                                                                    ================
    public String getUniqueName() {
        return _unifiedSchema.getCatalogSchema() + "." + _name;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return _unifiedSchema + "." + _name;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Database getDatabase() {
        return _database;
    }

    public void setDatabase(Database database) {
        this._database = database;
    }

    public String getProcedureName() {
        return _name;
    }

    public void setProcedureName(String procedureName) {
        this._name = procedureName;
    }

    public UnifiedSchema getUnifiedSchema() {
        return _unifiedSchema;
    }

    public void setUnifiedSchema(UnifiedSchema _unifiedSchema) {
        this._unifiedSchema = _unifiedSchema;
    }

    public BigDecimal getSourceLine() {
        return _sourceLine;
    }

    public void setSourceLine(BigDecimal sourceLine) {
        this._sourceLine = sourceLine;
    }

    public BigDecimal getSourceSize() {
        return _sourceSize;
    }

    public void setSourceSize(BigDecimal sourceSize) {
        this._sourceSize = sourceSize;
    }

    public BigDecimal getSourceHash() {
        return _sourceHash;
    }

    public void setSourceHash(BigDecimal sourceHash) {
        this._sourceHash = sourceHash;
    }

    public String getProcedureComment() {
        return _procedureComment;
    }

    public void setProcedureComment(String procedureComment) {
        this._procedureComment = procedureComment;
    }
}
