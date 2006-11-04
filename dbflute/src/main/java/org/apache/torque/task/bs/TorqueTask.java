package org.apache.torque.task.bs;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.Task;
import org.apache.torque.helper.TorqueBuildProperties;
import org.apache.torque.helper.TorqueTaskUtil;
import org.apache.torque.helper.properties.BasicProperties;

/**
 * Abstract DB meta texen task for Torque.
 * 
 * @author mkubo
 */
public abstract class TorqueTask extends Task {

    public static final Log _log = LogFactory.getLog(TorqueTask.class);

    public void setContextProperties(String file) {
        final Properties prop = TorqueTaskUtil.getBuildProperties(file, super.project);
        TorqueBuildProperties.getInstance().setProperties(prop);
    }

    protected TorqueBuildProperties getProperties() {
        return TorqueBuildProperties.getInstance();
    }

    protected BasicProperties getBasicProperties() {
        return getProperties().getBasicProperties();
    }
}