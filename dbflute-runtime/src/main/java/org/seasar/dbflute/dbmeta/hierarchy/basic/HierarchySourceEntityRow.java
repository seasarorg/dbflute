package org.seasar.dbflute.dbmeta.hierarchy.basic;


/**
 * @author DBFlute(AutoGenerator)
 */
public class HierarchySourceEntityRow implements org.seasar.dbflute.dbmeta.hierarchy.HierarchySourceRow {

    protected Object sourceBean;

    public HierarchySourceEntityRow(Object sourceBean) {
        this.sourceBean = sourceBean;
    }

    public Object extractColumnValue(org.seasar.dbflute.dbmeta.hierarchy.HierarchySourceColumn columnInfo) {
        if (!(columnInfo instanceof HierarchySourceEntityColumn)) {
            String msg = "The column info should be HierarchySourceEntityColumn! but: " + columnInfo;
            throw new IllegalStateException(msg);
        }
        final HierarchySourceEntityColumn sourceEntityColumn = (HierarchySourceEntityColumn) columnInfo;
        return invoke(sourceEntityColumn.findGetter(), sourceBean, new Object[] {});
    }

    private Object invoke(java.lang.reflect.Method method, Object target, Object[] args) {
        try {
            return method.invoke(target, args);
        } catch (java.lang.reflect.InvocationTargetException ex) {
            Throwable t = ex.getCause();
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            if (t instanceof Error) {
                throw (Error) t;
            }
            String msg = "target=" + target + " method=" + method + "-" + java.util.Arrays.asList(args);
            throw new RuntimeException(msg, ex);
        } catch (IllegalAccessException ex) {
            String msg = "target=" + target + " method=" + method + "-" + java.util.Arrays.asList(args);
            throw new RuntimeException(msg, ex);
        }
    }
}