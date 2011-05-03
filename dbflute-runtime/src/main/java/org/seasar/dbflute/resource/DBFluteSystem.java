package org.seasar.dbflute.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author jflute
 */
public class DBFluteSystem {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DBFluteSystem.class);

    // ===================================================================================
    //                                                                    Option Attribute
    //                                                                    ================
    protected static DBFluteCurrentProvider _currentProvider;

    protected static boolean _locked = true;

    // ===================================================================================
    //                                                                      Line Separator
    //                                                                      ==============
    public static String getBasicLn() {
        return "\n"; // LF is basic here
        // /- - - - - - - - - - - - - - - - - - - - - - - - - - -
        // The 'CR + LF' causes many trouble all over the world.
        //  e.g. Oracle stored procedure
        // - - - - - - - - - -/
    }

    // unused on DBFlute
    //public static String getSystemLn() {
    //    return System.getProperty("line.separator");
    //}

    // ===================================================================================
    //                                                                        Current Time
    //                                                                        ============
    public static Date currentDate() {
        return new Date(currentTimeMillis());
    }

    public static Timestamp currentTimestamp() {
        return new Timestamp(currentTimeMillis());
    }

    public static long currentTimeMillis() {
        final long millis;
        if (_currentProvider != null) {
            millis = _currentProvider.currentTimeMillis();
        } else {
            millis = System.currentTimeMillis();
        }
        return millis;
    }

    public static interface DBFluteCurrentProvider {
        long currentTimeMillis();
    }

    // ===================================================================================
    //                                                                    Operating System
    //                                                                    ================
    public static boolean isSystemWindowsOS() {
        final String osName = System.getProperty("os.name");
        return osName != null && osName.toLowerCase().contains("windows");
    }

    public static int executeSystemScript(File baseDir, String scriptName) { // simply (using default encoding)
        return executeSystemScript(baseDir, scriptName, null, "UTF-8");
    }

    protected static int executeSystemScript(File baseDir, String scriptName, Map<String, String> envMap,
            String encoding) {
        final List<String> cmdList = new ArrayList<String>();
        if (isSystemWindowsOS()) {
            cmdList.add("cmd.exe");
            cmdList.add("/c");
            cmdList.add(scriptName + ".bat");
        } else {
            cmdList.add("sh");
            cmdList.add(scriptName + ".sh");
        }
        final ProcessBuilder builder = new ProcessBuilder(cmdList);
        if (envMap != null && !envMap.isEmpty()) {
            builder.environment().putAll(envMap);
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
            final CommandConsoleReader reader = new CommandConsoleReader(stdin, encoding);
            reader.start();
            final int exitValue = process.waitFor();
            reader.join();
            return exitValue;
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

    protected static class CommandConsoleReader extends Thread {
        private BufferedReader _reader;

        public CommandConsoleReader(InputStream in, String encoding) {
            try {
                _reader = new BufferedReader(new InputStreamReader(in, encoding));
            } catch (UnsupportedEncodingException e) {
                String msg = "Failed to create a reader by encoding: " + encoding;
                throw new IllegalStateException(msg);
            }
        }

        @Override
        public void run() {
            final StringBuilder sb = new StringBuilder();
            try {
                while (true) {
                    final String line = _reader.readLine();
                    if (line == null) {
                        break;
                    }
                    sb.append(getBasicLn()).append(line);
                }
            } catch (IOException e) {
                String msg = "Failed to read the stream: " + _reader;
                throw new IllegalStateException(msg, e);
            } finally {
                _log.info(sb.toString().trim());
            }
        }
    }

    // ===================================================================================
    //                                                                     Option Accessor
    //                                                                     ===============
    public static void xlock() {
        _locked = true;
    }

    public static void xunlock() {
        _locked = false;
    }

    protected static void assertUnlocked() {
        if (_locked) {
            String msg = "DBFluteSystem was locked.";
            throw new IllegalStateException(msg);
        }
    }

    public static void xsetDBFluteCurrentProvider(DBFluteCurrentProvider currentProvider) {
        assertUnlocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting DBFluteCurrentProvider: " + currentProvider);
        }
        _currentProvider = currentProvider;
        xlock();
    }
}
