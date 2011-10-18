package org.seasar.dbflute.task;

/**
 * @author jflute
 * @since 0.9.9.1B (2011/10/17 Monday)
 */
public class DfDBFluteTaskStatus {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final DfDBFluteTaskStatus _instance = new DfDBFluteTaskStatus();

    public static final DfDBFluteTaskStatus getInstance() {
        return _instance;
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected TaskType _taskType;

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean isDocTask() {
        return TaskType.Doc.equals(_taskType);
    }

    public boolean isReplaceSchema() {
        return TaskType.ReplaceSchema.equals(_taskType);
    }

    // ===================================================================================
    //                                                                           Task Type
    //                                                                           =========
    public enum TaskType {
        JDBC, Doc, Generate, Sql2Entity, OutsideSqlTest, ReplaceSchema, Refresh, TakeAssert
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public TaskType getTaskType() {
        return _taskType;
    }

    public void setTaskType(TaskType taskType) {
        this._taskType = taskType;
    }
}
