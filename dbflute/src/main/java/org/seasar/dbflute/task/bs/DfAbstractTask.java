/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.task.bs;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.Task;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.properties.BasicProperties;
import org.seasar.dbflute.torque.DfAntTaskUtil;

/**
 * Abstract DB meta texen task for Torque.
 * 
 * @author mkubo
 */
public abstract class DfAbstractTask extends Task {

    public static final Log _log = LogFactory.getLog(DfAbstractTask.class);

    public void setContextProperties(String file) {
        final Properties prop = DfAntTaskUtil.getBuildProperties(file, super.project);
        DfBuildProperties.getInstance().setProperties(prop);
    }

    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    protected BasicProperties getBasicProperties() {
        return getProperties().getBasicProperties();
    }
}