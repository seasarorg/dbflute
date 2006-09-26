package org.apache.torque.task;

import org.apache.velocity.anakia.Escape;
import org.apache.velocity.context.Context;

/**
 * Documentation task for Torque.
 * 
 * @author Modified by mkubo
 */
public class TorqueDocumentationTask extends TorqueAbstractDbMetaTexenTask {

    private String _outputFormat;

    public TorqueDocumentationTask() {
    }

    public String getOutputFormat() {
        return _outputFormat;
    }

    public void setOutputFormat(String v) {
        _outputFormat = v;
    }

    /**
     * Init control context. {Override}
     * 
     * @return Context. 
     * @throws Exception
     */
    public Context initControlContext() throws Exception {
        super.initControlContext();
        _context.put("outputFormat", _outputFormat);
        _context.put("escape", new Escape());
        return _context;
    }
}
