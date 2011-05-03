package org.seasar.dbflute.helper.process;

/**
 * @author jflute
 * @since 0.9.8.3 (2011/05/03 Tuesday)
 */
public class ProcessResult {

    protected final String _processName;
    protected String _console;
    protected int _exitCode;
    protected boolean _systemMismatch;

    public ProcessResult(String processName) {
        _processName = processName;
    }

    public String getProcessName() {
        return _processName;
    }

    public String getConsole() {
        return _console;
    }

    public void setConsole(String console) {
        this._console = console;
    }

    public int getExitCode() {
        return _exitCode;
    }

    public void setExitCode(int exitCode) {
        this._exitCode = exitCode;
    }

    public boolean isSystemMismatch() {
        return _systemMismatch;
    }

    public void setSystemMismatch(boolean systemMismatch) {
        this._systemMismatch = systemMismatch;
    }
}
