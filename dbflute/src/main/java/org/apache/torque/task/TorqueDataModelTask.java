package org.apache.torque.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Data-model task for Torque.
 * <p>
 * This is main task in generator.
 * Build data-model(of Java or C#...) from DB-Meta.
 * 
 * @author Modified by mkubo
 */
public class TorqueDataModelTask extends TorqueAbstractDbMetaTexenTask {

    public static final Log _log = LogFactory.getLog(TorqueDataModelTask.class);
}