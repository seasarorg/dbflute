package org.seasar.dbflute.s2dao.sqlcommand;

import javax.sql.DataSource;

import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.twowaysql.SqlAnalyzer;
import org.seasar.dbflute.twowaysql.context.CommandContext;
import org.seasar.dbflute.twowaysql.context.CommandContextCreator;
import org.seasar.dbflute.twowaysql.node.Node;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public abstract class TnAbstractDynamicCommand extends TnAbstractSqlCommand {

	// ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Node rootNode;
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
	
	protected SqlAnalyzer createInternalSqlParser(String sql) {
	    return new SqlAnalyzer(sql, isBlockNullParameter());
	}

    protected boolean isBlockNullParameter() { // Extension Point!
        return false;
    }

    public CommandContext apply(Object[] args) { // It is necessary to be public!
        final CommandContext ctx = createCommandContext(args);
        rootNode.accept(ctx);
        return ctx;
    }

    protected CommandContext createCommandContext(Object[] args) {
	    return createCommandContextCreator().createCommandContext(args);
    }

	protected CommandContextCreator createCommandContextCreator() {
	    return new CommandContextCreator(argNames, argTypes);
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
