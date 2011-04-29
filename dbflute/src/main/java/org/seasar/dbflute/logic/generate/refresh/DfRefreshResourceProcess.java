package org.seasar.dbflute.logic.generate.refresh;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.properties.DfRefreshProperties;

/**
 * @author jflute
 */
public class DfRefreshResourceProcess {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfRefreshResourceProcess.class);

    // ===================================================================================
    //                                                                             Refresh
    //                                                                             =======
    public void refreshResources() {
        if (!isRefresh()) {
            return;
        }
        final List<String> projectNameList = getRefreshProjectNameList();
        for (String projectName : projectNameList) {
            doRefreshResources(projectName);
        }
    }

    protected void doRefreshResources(String projectName) {
        final StringBuilder sb = new StringBuilder().append("refresh?");
        sb.append(projectName).append("=INFINITE");

        final URL url = getRefreshRequestURL(sb.toString());
        if (url == null) {
            return;
        }

        InputStream is = null;
        try {
            _log.info("/- - - - - - - - - - - - - - - - - - - - - - - -");
            _log.info("...Refreshing the project: " + projectName);
            URLConnection connection = url.openConnection();
            connection.setReadTimeout(getRefreshRequestReadTimeout());
            connection.connect();
            is = connection.getInputStream();
            _log.info("");
            _log.info("    --> OK, Look at the refreshed project!");
            _log.info("- - - - - - - - - -/");
            _log.info("");
        } catch (IOException ignored) {
            _log.info("");
            _log.info("    --> Oh, no! " + ignored.getMessage() + ": " + url);
            _log.info("- - - - - - - - - -/");
            _log.info("");
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    protected boolean isRefresh() {
        final DfRefreshProperties prop = getRefreshProperties();
        return prop.hasRefreshDefinition();
    }

    protected int getRefreshRequestReadTimeout() {
        return 3 * 1000;
    }

    protected List<String> getRefreshProjectNameList() {
        final DfRefreshProperties prop = getRefreshProperties();
        return prop.getProjectNameList();
    }

    protected URL getRefreshRequestURL(String path) {
        final DfRefreshProperties prop = getRefreshProperties();
        String requestUrl = prop.getRequestUrl();
        if (requestUrl.length() > 0) {
            if (!requestUrl.endsWith("/")) {
                requestUrl = requestUrl + "/";
            }
            try {
                return new URL(requestUrl + path);
            } catch (MalformedURLException e) {
                _log.warn("The URL was invalid: " + requestUrl, e);
                return null;
            }
        } else {
            return null;
        }
    }

    protected DfRefreshProperties getRefreshProperties() {
        return DfBuildProperties.getInstance().getRefreshProperties();
    }
}
