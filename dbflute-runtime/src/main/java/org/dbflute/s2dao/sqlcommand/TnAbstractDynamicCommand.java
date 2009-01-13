package org.dbflute.s2dao.sqlcommand;

import javax.sql.DataSource;

import org.dbflute.jdbc.StatementFactory;
import org.dbflute.twowaysql.SqlAnalyzer;
import org.dbflute.twowaysql.context.CommandContext;
import org.dbflute.twowaysql.context.CommandContextCreator;
import org.dbflute.twowaysql.node.Node;

/**
 * @author DBFlute(AutoGenerator)
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
