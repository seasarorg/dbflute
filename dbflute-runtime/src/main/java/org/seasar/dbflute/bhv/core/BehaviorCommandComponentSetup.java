package org.seasar.dbflute.bhv.core;

import javax.sql.DataSource;

import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaDataFactory;
import org.seasar.dbflute.s2dao.valuetype.TnValueTypeFactory;

/**
 * @author jflute
 */
public interface BehaviorCommandComponentSetup {

    public void setDataSource(DataSource dataSource);
    public void setStatementFactory(StatementFactory statementFactory);
    public void setBeanMetaDataFactory(TnBeanMetaDataFactory beanMetaDataFactory);
    public void setValueTypeFactory(TnValueTypeFactory valueTypeFactory);
    public void setSqlFileEncoding(String sqlFileEncoding);
}
