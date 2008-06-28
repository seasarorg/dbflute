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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.EngineException;

/**
 * A <code>NameGenerator</code> implementation for table-specific
 * constraints.  Conforms to the maximum column name length for the
 * type of database in use.
 * @author Modified by jflute
 */
public class ConstraintNameGenerator implements NameGenerator {

    /** Log instance. */
    private static Log log = LogFactory.getLog(ConstraintNameGenerator.class);

    /**
     * First element of <code>inputs</code> should be of type {@link
     * org.apache.torque.engine.database.model.Database}, second
     * should be a table name, third is the type identifier (spared if
     * trimming is necessary due to database type length constraints),
     * and the fourth is a <code>Integer</code> indicating the number
     * of this contraint.
     *
     * @see org.apache.torque.engine.database.model.NameGenerator
     */
    public String generateName(List<?> inputs) throws EngineException {
        final StringBuffer name = new StringBuffer();
        final Database db = (Database) inputs.get(0);
        name.append((String) inputs.get(1));
        final String namePostfix = (String) inputs.get(2);
        final String constraintNbr = inputs.get(3).toString();

        // Calculate maximum RDBMS-specific column character limit.
        int maxBodyLength = -1;
        try {
            int maxColumnNameLength = Integer.parseInt(db.getProperty("maxColumnNameLength"));
            maxBodyLength = (maxColumnNameLength - namePostfix.length() - constraintNbr.length() - 2);

            if (log.isDebugEnabled()) {
                log.debug("maxColumnNameLength=" + maxColumnNameLength + " maxBodyLength=" + maxBodyLength);
            }
        } catch (EngineException e) {
            log.error(e.getMessage(), e);
        } catch (NumberFormatException maxLengthUnknown) {
        }

        // Do any necessary trimming.
        if (maxBodyLength != -1 && name.length() > maxBodyLength) {
            name.setLength(maxBodyLength);
        }

        name.append(STD_SEPARATOR_CHAR).append(namePostfix).append(STD_SEPARATOR_CHAR).append(constraintNbr);

        return name.toString();
    }
}
