package org.dbflute.bhv.core;

import javax.sql.DataSource;

import org.dbflute.jdbc.StatementFactory;
import org.dbflute.s2dao.metadata.TnBeanMetaDataFactory;
import org.dbflute.s2dao.valuetype.TnValueTypeFactory;

/**
 * @author DBFlute(AutoGenerator)
 */
public interface BehaviorCommandComponentSetup {

    public void setDataSource(DataSource dataSource);
    public void setStatementFactory(StatementFactory statementFactory);
    public void setBeanMetaDataFactory(TnBeanMetaDataFactory beanMetaDataFactory);
    public void setValueTypeFactory(TnValueTypeFactory valueTypeFactory);
    public void setSqlFileEncoding(String sqlFileEncoding);
}
