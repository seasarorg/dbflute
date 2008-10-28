package org.seasar.dbflute.helper.dataset.states;

import javax.sql.DataSource;

import org.seasar.dbflute.helper.dataset.DataRow;

/**
 * Row States. {Refer to S2Container}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public abstract class AbstractRowState implements RowState {

    AbstractRowState() {
    }

    public void update(DataSource dataSource, DataRow row) {
        throw new UnsupportedOperationException();
        //        SqlContext ctx = getSqlContext(row);
        //        UpdateHandler handler = new BasicUpdateHandler(dataSource, ctx.getSql());
        //        execute(handler, ctx.getArgs(), ctx.getArgTypes());
    }

    // protected void execute(UpdateHandler handler, Object[] args, Class<?>[] argTypes) {
    //     handler.execute(args, argTypes);
    // }
    
    protected abstract SqlContext getSqlContext(DataRow row);
}