package org.seasar.dbflute.task.replaceschema;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileFireResult;
import org.seasar.dbflute.helper.token.line.LineToken;
import org.seasar.dbflute.helper.token.line.LineTokenizingOption;
import org.seasar.dbflute.helper.token.line.impl.LineTokenImpl;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfAbstractSchemaTaskFinalInfo;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfCreateSchemaFinalInfo;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfLoadDataFinalInfo;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfReplaceSchemaFinalInfo;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfTakeFinallyFinalInfo;
import org.seasar.dbflute.task.bs.DfAbstractTask;

/**
 * @author jflute
 * @since 0.7.9 (2008/08/22 Friday)
 */
public abstract class DfAbstractReplaceSchemaTask extends DfAbstractTask {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final String CREATE_SCHEMA_LOG_PATH = "./log/create-schema.log";
    protected static final String LOAD_DATA_LOG_PATH = "./log/load-data.log";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DfReplaceSchemaFinalInfo _replaceSchemaFinalInfo;

    // ===================================================================================
    //                                                                 DataSource Override
    //                                                                 ===================
    @Override
    protected boolean isUseDataSource() {
        return true;
    }

    // ===================================================================================
    //                                                                      Various Common
    //                                                                      ==============
    protected String resolveTerminater4Tool() {
        return getBasicProperties().isDatabaseOracle() ? "/" : null;
    }

    protected boolean isDbCommentLineForIrregularPattern(String line) {
        // for irregular pattern
        line = line.trim().toLowerCase();
        if (getBasicProperties().isDatabaseMySQL()) {
            if (line.contains("comment='") || line.contains("comment = '") || line.contains(" comment '")) {
                return true;
            }
        }
        if (getBasicProperties().isDatabaseSQLServer()) {
            if (line.startsWith("exec sys.sp_addextendedproperty @name=n'ms_description'")) {
                return true;
            }
        }
        return false;
    }

    // ===================================================================================
    //                                                                         Result Dump
    //                                                                         ===========
    protected void dumpProcessResult(File dumpFile, String resultMessage, boolean failure, String detailMessage) {
        if (dumpFile.exists()) {
            boolean deleted = dumpFile.delete();
            if (!deleted) {
                return; // skip to dump!
            }
        }
        if (resultMessage == null || resultMessage.trim().length() == 0) {
            return; // nothing to dump!
        }
        BufferedWriter bw = null;
        try {
            final StringBuilder contentsSb = new StringBuilder();
            contentsSb.append(resultMessage).append(ln()).append(failure);
            if (detailMessage != null && detailMessage.trim().length() > 0) {
                contentsSb.append(ln()).append(detailMessage);
            }
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dumpFile), "UTF-8"));
            bw.write(contentsSb.toString());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    // ===================================================================================
    //                                                                          Final Info
    //                                                                          ==========
    @Override
    protected String getFinalInformation() {
        return buildReplaceSchemaFinalMessage(getReplaceSchemaFinalInfo()); // The argument cannot be null!
    }

    protected DfReplaceSchemaFinalInfo getReplaceSchemaFinalInfo() {
        if (_replaceSchemaFinalInfo != null) {
            return _replaceSchemaFinalInfo;
        }
        _replaceSchemaFinalInfo = createReplaceSchemaFinalInfo();
        return _replaceSchemaFinalInfo;
    }

    protected DfReplaceSchemaFinalInfo createReplaceSchemaFinalInfo() {
        final DfCreateSchemaFinalInfo createSchemaFinalInfo = extractCreateSchemaFinalInfo();
        final DfLoadDataFinalInfo loadDataFinalInfo = extractLoadDataFinalInfo();
        final DfTakeFinallyFinalInfo takeFinallyFinalInfo = getTakeFinallyFinalInfo();
        return new DfReplaceSchemaFinalInfo(createSchemaFinalInfo, loadDataFinalInfo, takeFinallyFinalInfo);
    }

    protected DfCreateSchemaFinalInfo extractCreateSchemaFinalInfo() {
        final DfCreateSchemaFinalInfo finalInfo = new DfCreateSchemaFinalInfo();
        reflectDumpedFinalInfo(CREATE_SCHEMA_LOG_PATH, finalInfo);
        return finalInfo;
    }

    protected DfLoadDataFinalInfo extractLoadDataFinalInfo() {
        final DfLoadDataFinalInfo finalInfo = new DfLoadDataFinalInfo();
        reflectDumpedFinalInfo(LOAD_DATA_LOG_PATH, finalInfo);
        return finalInfo;
    }

    protected void reflectDumpedFinalInfo(String path, DfAbstractSchemaTaskFinalInfo finalInfo) {
        final File file = new File(path);
        if (!file.exists()) {
            return;
        }
        BufferedReader br = null;
        try {
            final FileInputStream fis = new FileInputStream(file);
            br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));

            // - - - - - - - - - - - -
            // line1: resultMessage
            // line2: existsError
            // line3-x: detailMessage
            // - - - - - - - - - - - -
            final String line = br.readLine();
            if (line == null) {
                return;
            }
            finalInfo.setResultMessage(line);
            final String line2 = br.readLine();
            if (line2 != null) {
                while (true) {
                    String line3 = br.readLine();
                    if (line3 == null) {
                        break;
                    }
                    finalInfo.addDetailMessage(line3);
                }
            }
            finalInfo.setFailure(line2 != null && line2.trim().equalsIgnoreCase("true"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ignored) {
                }
            }
            if (file.exists()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    // ignored
                }
            }
        }
    }

    protected DfTakeFinallyFinalInfo getTakeFinallyFinalInfo() {
        return null; // as default (should be overridden if take-finally)
    }

    protected DfTakeFinallyFinalInfo extractTakeFinallyFinalInfo(DfSqlFileFireResult takeFinallyFireResult) {
        if (takeFinallyFireResult == null) {
            return null;
        }
        final DfTakeFinallyFinalInfo finalInfo = new DfTakeFinallyFinalInfo();
        finalInfo.setResultMessage(takeFinallyFireResult.getResultMessage());
        finalInfo.setFailure(takeFinallyFireResult.existsError());
        final String detailMessage = takeFinallyFireResult.getDetailMessage();
        if (detailMessage != null && detailMessage.trim().length() > 0) {
            final LineToken lineToken = new LineTokenImpl();
            final LineTokenizingOption lineTokenizingOption = new LineTokenizingOption();
            lineTokenizingOption.setDelimiter(ln());
            final List<String> tokenizedList = lineToken.tokenize(detailMessage, lineTokenizingOption);
            for (String tokenizedElement : tokenizedList) {
                finalInfo.addDetailMessage(tokenizedElement);
            }
        }
        return finalInfo;
    }

    protected String buildReplaceSchemaFinalMessage(DfReplaceSchemaFinalInfo replaceSchemaFinalInfo) {
        if (replaceSchemaFinalInfo == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        boolean firstDone = false;

        // Create Schema
        {
            final DfCreateSchemaFinalInfo createSchemaFinalInfo = replaceSchemaFinalInfo.getCreateSchemaFinalInfo();
            if (createSchemaFinalInfo != null && createSchemaFinalInfo.isValidInfo()) {
                if (firstDone) {
                    sb.append(ln()).append(ln());
                }
                firstDone = true;
                buildSchemaTaskContents(sb, createSchemaFinalInfo);
            }
        }

        // Load Data
        {
            final DfLoadDataFinalInfo loadDataFinalInfo = replaceSchemaFinalInfo.getLoadDataFinalInfo();
            if (loadDataFinalInfo != null && loadDataFinalInfo.isValidInfo()) {
                if (firstDone) {
                    sb.append(ln()).append(ln());
                }
                firstDone = true;
                buildSchemaTaskContents(sb, loadDataFinalInfo);
            }
        }

        // Take Finally
        {
            final DfTakeFinallyFinalInfo takeFinallyFinalInfo = replaceSchemaFinalInfo.getTakeFinallyFinalInfo();
            if (takeFinallyFinalInfo != null && takeFinallyFinalInfo.isValidInfo()) {
                if (firstDone) {
                    sb.append(ln()).append(ln());
                }
                firstDone = true;
                buildSchemaTaskContents(sb, takeFinallyFinalInfo);
            }
        }

        if (replaceSchemaFinalInfo.hasFailure()) {
            sb.append(ln()).append("    * * * * * *");
            sb.append(ln()).append("    * Failure *");
            sb.append(ln()).append("    * * * * * *");
        }
        return sb.toString();
    }

    protected void buildSchemaTaskContents(StringBuilder sb, DfAbstractSchemaTaskFinalInfo finalInfo) {
        sb.append(" ").append(finalInfo.getResultMessage());
        final List<String> detailMessageList = finalInfo.getDetailMessageList();
        for (String detailMessage : detailMessageList) {
            sb.append(ln()).append("  ").append(detailMessage);
        }
    }
}
