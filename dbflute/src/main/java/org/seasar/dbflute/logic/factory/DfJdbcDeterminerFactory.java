package org.seasar.dbflute.logic.factory;

import org.seasar.dbflute.helper.jdbc.determiner.DfJdbcDeterminer;
import org.seasar.dbflute.helper.jdbc.determiner.DfJdbcDeterminerDefault;
import org.seasar.dbflute.helper.jdbc.determiner.DfJdbcDeterminerDerby;
import org.seasar.dbflute.helper.jdbc.determiner.DfJdbcDeterminerMsAccess;
import org.seasar.dbflute.properties.DfBasicProperties;

/**
 * @author jflute
 */
public class DfJdbcDeterminerFactory {

    protected DfBasicProperties _basicProperties;

    public DfJdbcDeterminerFactory(DfBasicProperties basicProperties) {
        _basicProperties = basicProperties;
    }

    public DfJdbcDeterminer createJdbcDeterminer() {
        if (_basicProperties.isDatabaseDerby()) {
            return new DfJdbcDeterminerDerby();
        }
        if (_basicProperties.isDatabaseMSAccess()) {
            return new DfJdbcDeterminerMsAccess();
        }
        return new DfJdbcDeterminerDefault();
    }
}
