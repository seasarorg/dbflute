package org.seasar.dbflute.helper.process;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.seasar.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 * @since 0.9.8.3 (2011/05/03 Tuesday)
 */
public class SystemScript {

    public static final String WINDOWS_BATCH_EXT = ".bat";
    public static final String SHELL_SCRIPT_EXT = ".sh";

    protected static final List<String> SUPPORTED_EXT_LIST;
    static {
        final List<String> tmpList = DfCollectionUtil.newArrayList(WINDOWS_BATCH_EXT, SHELL_SCRIPT_EXT);
        SUPPORTED_EXT_LIST = Collections.unmodifiableList(tmpList);
    }

    public static List<String> getSupportedExtList() {
        return SUPPORTED_EXT_LIST;
    }

    protected String _consoleEncoding;
    protected Map<String, String> _environmentMap;

    public ProcessResult execute(File baseDir, String scriptName) {
        final ProcessResult result = new ProcessResult(scriptName);
        final List<String> cmdList = new ArrayList<String>();
        if (isSystemWindowsOS()) {
            if (scriptName.endsWith(WINDOWS_BATCH_EXT)) {
                cmdList.add("cmd.exe");
                cmdList.add("/c");
                cmdList.add(scriptName);
            }
        } else {
            if (scriptName.endsWith(SHELL_SCRIPT_EXT)) {
                cmdList.add("sh");
                cmdList.add(scriptName);
            }
        }
        if (cmdList.isEmpty()) {
            result.setSystemMismatch(true);
            return result;
        }
        final ProcessBuilder builder = new ProcessBuilder(cmdList);
        if (_environmentMap != null && !_environmentMap.isEmpty()) {
            builder.environment().putAll(_environmentMap);
        }
        final Process process;
        try {
            process = builder.directory(baseDir).redirectErrorStream(true).start();
        } catch (IOException e) {
            String msg = "Failed to execute the command: " + scriptName;
            throw new IllegalStateException(msg, e);
        }

        InputStream stdin = null;
        try {
            stdin = process.getInputStream();
            final String encoding = _consoleEncoding != null ? _consoleEncoding : "UTF-8";
            final ProcessConsoleReader reader = new ProcessConsoleReader(stdin, encoding);
            reader.start();
            final int exitCode = process.waitFor();
            reader.join();
            final String console = reader.read();
            result.setConsole(console);
            result.setExitCode(exitCode);
            return result;
        } catch (InterruptedException e) {
            String msg = "The execution was interrupted: " + scriptName;
            throw new IllegalStateException(msg, e);
        } finally {
            if (stdin != null) {
                try {
                    stdin.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    protected boolean isSystemWindowsOS() {
        final String osName = System.getProperty("os.name");
        return osName != null && osName.toLowerCase().contains("windows");
    }

    public String getConsoleEncoding() {
        return _consoleEncoding;
    }

    public void setConsoleEncoding(String consoleEncoding) {
        this._consoleEncoding = consoleEncoding;
    }

    public Map<String, String> getEnvironmentMap() {
        return _environmentMap;
    }

    public void setEnvironmentMap(Map<String, String> environmentMap) {
        this._environmentMap = environmentMap;
    }
}
