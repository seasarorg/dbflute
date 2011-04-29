package org.seasar.dbflute.logic.replaceschema.process;

import java.util.ArrayList;
import java.util.List;

import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.jdbc.DfRunnerInformation;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileFireResult;
import org.seasar.dbflute.helper.token.line.LineToken;
import org.seasar.dbflute.helper.token.line.LineTokenizingOption;
import org.seasar.dbflute.helper.token.line.impl.LineTokenImpl;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfDatabaseProperties;
import org.seasar.dbflute.properties.DfReplaceSchemaProperties;
import org.seasar.dbflute.properties.facade.DfDatabaseTypeFacadeProp;
import org.seasar.dbflute.properties.facade.DfLanguageTypeFacadeProp;

/**
 * @author jflute
 * @since 0.9.8.3 (2011/04/29 Friday)
 */
public class DfAbstractReplaceSchemaProcess {

    // ===================================================================================
    //                                                                     SQL File Runner
    //                                                                     ===============
    protected DfRunnerInformation createRunnerInformation() {
        final DfRunnerInformation runInfo = new DfRunnerInformation();
        final DfDatabaseProperties prop = getDatabaseProperties();
        runInfo.setDriver(prop.getDatabaseDriver());
        runInfo.setUrl(prop.getDatabaseUrl());
        runInfo.setUser(prop.getDatabaseUser());
        runInfo.setPassword(prop.getDatabasePassword());
        runInfo.setEncoding(getReplaceSchemaProperties().getSqlFileEncoding());
        runInfo.setAutoCommit(true); // fixed as no transaction
        runInfo.setErrorContinue(getReplaceSchemaProperties().isErrorContinue());
        runInfo.setRollbackOnly(false);
        return runInfo;
    }

    protected String resolveTerminater4Tool() {
        return getDatabaseTypeFacadeProp().isDatabaseOracle() ? "/" : null;
    }

    protected boolean isDbCommentLineForIrregularPattern(String line) {
        // for irregular pattern
        line = line.trim().toLowerCase();
        if (getDatabaseTypeFacadeProp().isDatabaseMySQL()) {
            if (line.contains("comment='") || line.contains("comment = '") || line.contains(" comment '")) {
                return true;
            }
        }
        if (getDatabaseTypeFacadeProp().isDatabaseSQLServer()) {
            if (line.startsWith("exec sys.sp_addextendedproperty @name=n'ms_description'")) {
                return true;
            }
        }
        return false;
    }

    // ===================================================================================
    //                                                                          Final Info
    //                                                                          ==========
    protected List<String> extractDetailMessageList(DfSqlFileFireResult fireResult) {
        final List<String> detailMessageList = new ArrayList<String>();
        final String detailMessage = fireResult.getDetailMessage();
        if (detailMessage != null && detailMessage.trim().length() > 0) {
            final LineToken lineToken = new LineTokenImpl();
            final LineTokenizingOption lineTokenizingOption = new LineTokenizingOption();
            lineTokenizingOption.setDelimiter(ln());
            final List<String> tokenizedList = lineToken.tokenize(detailMessage, lineTokenizingOption);
            for (String tokenizedElement : tokenizedList) {
                detailMessageList.add(tokenizedElement);
            }
        }
        return detailMessageList;
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected static DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    protected static DfReplaceSchemaProperties getReplaceSchemaProperties() {
        return DfBuildProperties.getInstance().getReplaceSchemaProperties();
    }

    protected static DfBasicProperties getBasicProperties() {
        return getProperties().getBasicProperties();
    }

    protected static DfDatabaseTypeFacadeProp getDatabaseTypeFacadeProp() {
        return getBasicProperties().getDatabaseTypeFacadeProp();
    }

    protected static DfLanguageTypeFacadeProp getLanguageTypeFacadeProp() {
        return getBasicProperties().getLanguageTypeFacadeProp();
    }

    protected static DfDatabaseProperties getDatabaseProperties() {
        return getProperties().getDatabaseProperties();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return "\n";
    }
}
