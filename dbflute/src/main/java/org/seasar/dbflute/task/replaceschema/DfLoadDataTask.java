package org.seasar.dbflute.task.replaceschema;

import org.seasar.dbflute.logic.replaceschema.process.DfLoadDataProcess;

public class DfLoadDataTask extends DfAbstractReplaceSchemaTask {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean _validTaskEndInformation = true;

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Override
    protected void doExecute() {
        final DfLoadDataProcess main = DfLoadDataProcess.createAsCore(getDataSource());
        main.execute();
    }

    @Override
    protected boolean isValidTaskEndInformation() {
        return _validTaskEndInformation;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setValidTaskEndInformation(String validTaskEndInformation) {
        this._validTaskEndInformation = validTaskEndInformation != null
                && validTaskEndInformation.trim().equalsIgnoreCase("true");
    }
}
