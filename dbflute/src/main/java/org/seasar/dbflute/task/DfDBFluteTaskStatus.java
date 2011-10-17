package org.seasar.dbflute.task;

/**
 * @author jflute
 * @since 0.9.9.1B (2011/10/17 Monday)
 */
public class DfDBFluteTaskStatus {

    private static final DfDBFluteTaskStatus _instance = new DfDBFluteTaskStatus();

    public static final DfDBFluteTaskStatus getInstance() {
        return _instance;
    }

    protected TaskType taskType;

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public enum TaskType {
        JDBC, Doc, Generate, Sql2Entity, OutsideSqlTest, ReplaceSchema, Refresh, TakeAssert
    }
}
