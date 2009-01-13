package org.dbflute.twowaysql.context;

import org.dbflute.twowaysql.context.impl.TnCommandContextImpl;

/**
 * @author DBFlute(AutoGenerator)
 */
public class TnCommandContextCreator {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String[] argNames;
    protected Class<?>[] argTypes;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnCommandContextCreator(String[] argNames, Class<?>[] argTypes) {
        this.argNames = (argNames != null ? argNames : new String[0]);
        this.argTypes = (argTypes != null ? argTypes : new Class[0]);
    }

    // ===================================================================================
    //                                                                              Create
    //                                                                              ======
    public TnCommandContext createCommandContext(Object[] args) {
        final TnCommandContext ctx = new TnCommandContextImpl();
        if (args != null) {
            for (int i = 0; i < args.length; ++i) {
                Class<?> argType = null;
                if (args[i] != null) {
                    if (i < argTypes.length) {
                        argType = argTypes[i];
                    } else if (args[i] != null) {
                        argType = args[i].getClass();
                    }
                }
                if (i < argNames.length) {
                    ctx.addArg(argNames[i], args[i], argType);
                } else {
                    ctx.addArg("$" + (i + 1), args[i], argType);
                }
            }
        }
        return ctx;
    }
}
