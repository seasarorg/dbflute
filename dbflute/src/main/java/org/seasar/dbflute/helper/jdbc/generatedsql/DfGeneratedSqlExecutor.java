package org.seasar.dbflute.helper.jdbc.generatedsql;

public interface DfGeneratedSqlExecutor {
    public void execute(String sql, String aliasName);

    public void execute(String sql, String aliasName, DfGeneratedSqlExecuteOption option);

    public static class DfGeneratedSqlExecuteOption {
        protected boolean errorContinue;

        public boolean isErrorContinue() {
            return errorContinue;
        }

        public void setErrorContinue(boolean errorContinue) {
            this.errorContinue = errorContinue;
        }
    }
}
