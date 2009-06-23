package org.seasar.dbflute.friends.velocity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.SimpleLog4JLogSystem;
import org.seasar.dbflute.friends.log4j.DfOriginalRollingFileAppender;

/**
 * DBFlute original LogSystem using Log4j that extends the SimpleLog4JLogSystem of Velocity. <br />
 * Thanks, Velocity!
 * @author jflute
 * @since 0.9.5.1 (2009/06/23 Tuesday)
 */
public class DfOriginalLog4JLogSystem extends SimpleLog4JLogSystem {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance for DBFlute log. */
    private static final Log _log = LogFactory.getLog(DfOriginalLog4JLogSystem.class);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfOriginalLog4JLogSystem() {
    }

    // ===================================================================================
    //                                                                 Initialize Override
    //                                                                 ===================
    public void init(RuntimeServices rs) {
        final String logfile = "./log/velocity.log";
        try {
            logger = Logger.getLogger(getClass().getName());
            logger.setAdditivity(false);
            logger.setLevel(Level.DEBUG);

            final DfOriginalRollingFileAppender appender = createOriginalRollingFileAppender(logfile);
            appender.setMaxBackupIndex(2);
            appender.setMaximumFileSize(100000);
            logger.addAppender(appender);

            logVelocityMessage(0, ""); // as begin mark.
            logVelocityMessage(0, getClass().getSimpleName() + " initialized using logfile '" + logfile + "'");
        } catch (Exception e) {
            _log.warn("PANIC : error configuring " + getClass().getSimpleName() + " : ", e);
        }
    }

    protected DfOriginalRollingFileAppender createOriginalRollingFileAppender(String logfile) throws Exception {
        return new DfOriginalRollingFileAppender(new PatternLayout("%d - %m%n"), logfile, true);
    }
}
