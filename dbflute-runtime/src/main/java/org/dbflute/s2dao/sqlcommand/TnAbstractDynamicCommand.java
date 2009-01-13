package org.dbflute.s2dao.sqlcommand;

import javax.sql.DataSource;

import org.dbflute.jdbc.StatementFactory;
import org.dbflute.twowaysql.TnSqlParser;
import org.dbflute.twowaysql.context.TnCommandContext;
import org.dbflute.twowaysql.context.TnCommandContextCreator;
import org.dbflute.twowaysql.node.TnNode;

/**
 * @author DBFlute(AutoGenerator)
 */
public abstract class TnAbstractDynamicCommand extends TnAbstractSqlCommand {

	// ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected TnNode rootNode;
    protected String[] argNames = new String[0];
    protected Class<?>[] argTypes = new Class[0];

	// ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnAbstractDynamicCommand(DataSource dataSource, StatementFactory statementFactory) {
        super(dataSource, statementFactory);
    }

	// ===================================================================================
    //                                                                        Sql Handling
    //                                                                        ============
    public void setSql(String sql) {
        super.setSql(sql);
        this.rootNode = createInternalSqlParser(sql).parse();
    }
	
	protected TnSqlParser createInternalSqlParser(String sql) {
	    return new TnSqlParser(sql, isBlockNullParameter());
	}

    protected boolean isBlockNullParameter() { // Extension Point!
        return false;
    }

    public TnCommandContext apply(Object[] args) { // It is necessary to be public!
        final TnCommandContext ctx = createCommandContext(args);
        rootNode.accept(ctx);
        return ctx;
    }

    protected TnCommandContext createCommandContext(Object[] args) {
	    return createCommandContextCreator().createCommandContext(args);
    }

	protected TnCommandContextCreator createCommandContextCreator() {
	    return new TnCommandContextCreator(argNames, argTypes);
	}
	
	// ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String[] getArgNames() {
        return argNames;
    }

    public void setArgNames(String[] argNames) {
        this.argNames = argNames;
    }

    public Class<?>[] getArgTypes() {
        return argTypes;
    }

    public void setArgTypes(Class<?>[] argTypes) {
        this.argTypes = argTypes;
    }
}
