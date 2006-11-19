package org.apache.torque.task;

import org.apache.velocity.anakia.Escape;
import org.apache.velocity.context.Context;
import org.seasar.dbflute.task.bs.DfAbstractDbMetaTexenTask;

/**
 * Documentation task for Torque.
 * 
 * @author Modified by mkubo
 */
public class TorqueDocumentationTask extends DfAbstractDbMetaTexenTask {

    private String _outputFormat;

    public String getOutputFormat() {
        return _outputFormat;
    }

    public void setOutputFormat(String v) {
        _outputFormat = v;
    }

    public Context initControlContext() throws Exception {
        super.initControlContext();
        _context.put("outputFormat", _outputFormat);
        _context.put("escape", new Escape());
        return _context;
    }
}
