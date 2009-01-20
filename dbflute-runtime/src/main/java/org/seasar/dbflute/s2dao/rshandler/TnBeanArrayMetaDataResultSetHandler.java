package org.seasar.dbflute.s2dao.rshandler;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.dbflute.s2dao.rowcreator.TnRelationRowCreator;
import org.seasar.dbflute.s2dao.rowcreator.TnRowCreator;

/**
 * @author jflute
 */
@SuppressWarnings("unchecked")
public class TnBeanArrayMetaDataResultSetHandler extends TnBeanListMetaDataResultSetHandler {

	// ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
	 * @param beanMetaData Bean meta data. (NotNull)
     * @param rowCreator Row creator. (NotNull)
     * @param relationRowCreator Relation row creator. (NotNul)
     */
    public TnBeanArrayMetaDataResultSetHandler(TnBeanMetaData beanMetaData, TnRowCreator rowCreator, TnRelationRowCreator relationRowCreator) {
        super(beanMetaData, rowCreator, relationRowCreator);
    }
	
	// ===================================================================================
    //                                                                              Handle
    //                                                                              ======
    public Object handle(ResultSet rs) throws SQLException {
        List list = (List) super.handle(rs);
        return list.toArray((Object[]) Array.newInstance(getBeanMetaData().getBeanClass(), list.size()));
    }
}
