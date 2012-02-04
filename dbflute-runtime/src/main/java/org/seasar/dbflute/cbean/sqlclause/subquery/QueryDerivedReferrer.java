package org.seasar.dbflute.cbean.sqlclause.subquery;

import java.util.List;

import org.seasar.dbflute.cbean.cipher.GearedCipherManager;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.name.ColumnRealName;
import org.seasar.dbflute.dbmeta.name.ColumnRealNameProvider;
import org.seasar.dbflute.dbmeta.name.ColumnSqlName;
import org.seasar.dbflute.dbmeta.name.ColumnSqlNameProvider;

/**
 * @author jflute
 * @since 0.9.7.2 (2010/06/20 Sunday)
 */
public class QueryDerivedReferrer extends DerivedReferrer {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _operand;
    protected final Object _value; // NullAllowed: when IsNull or IsNotNull
    protected final String _parameterPath;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public QueryDerivedReferrer(SubQueryPath subQueryPath, ColumnRealNameProvider localRealNameProvider,
            ColumnSqlNameProvider subQuerySqlNameProvider, int subQueryLevel, SqlClause subQuerySqlClause,
            String subQueryIdentity, DBMeta subQueryDBMeta, GearedCipherManager cipherManager,
            String mainSubQueryIdentity, String operand, Object value, String parameterPath) {
        super(subQueryPath, localRealNameProvider, subQuerySqlNameProvider, subQueryLevel, subQuerySqlClause,
                subQueryIdentity, subQueryDBMeta, cipherManager, mainSubQueryIdentity);
        _operand = operand;
        _value = value;
        _parameterPath = parameterPath;
    }

    // ===================================================================================
    //                                                                        Build Clause
    //                                                                        ============
    @Override
    protected String doBuildDerivedReferrer(String function, ColumnRealName columnRealName,
            ColumnSqlName relatedColumnSqlName, String subQueryClause, String beginMark, String endMark,
            String endIndent) {
        final StringBuilder sb = new StringBuilder();
        sb.append("(").append(beginMark).append(subQueryClause);
        sb.append(ln()).append(endIndent).append(") ");
        sb.append(_operand);
        if (_value != null) {
            final String prefix = "/*pmb.";
            final String suffix = "*/null";
            final String parameter;
            if (isOperandBetween() && isValueListType()) {
                final String fromParameter = buildListParameter(prefix, 0, suffix);
                final String toParameter = buildListParameter(prefix, 1, suffix);
                parameter = fromParameter + " and " + toParameter;
            } else {
                parameter = prefix + _parameterPath + suffix;
            }
            sb.append(" ").append(parameter);
        }
        sb.append(" ").append(endMark);
        return sb.toString();
    }

    protected boolean isOperandBetween() {
        return "between".equalsIgnoreCase(_operand);
    }

    protected boolean isValueListType() {
        return _value instanceof List<?>;
    }

    protected String buildListParameter(String prefix, int index, String suffix) {
        return prefix + _parameterPath + ".get(" + index + ")" + suffix;
    }

    @Override
    protected void throwDerivedReferrerInvalidColumnSpecificationException(String function) {
        createCBExThrower().throwQueryDerivedReferrerInvalidColumnSpecificationException(function);
    }

    @Override
    protected void doAssertDerivedReferrerColumnType(String function, String derivedColumnDbName,
            Class<?> derivedColumnType) {
        final Object value = _value;
        if ("sum".equalsIgnoreCase(function) || "avg".equalsIgnoreCase(function)) {
            if (!Number.class.isAssignableFrom(derivedColumnType)) {
                throwQueryDerivedReferrerUnmatchedColumnTypeException(function, derivedColumnDbName, derivedColumnType);
            }
        }
        if (value != null) {
            final Class<?> parameterType = value.getClass();
            if (String.class.isAssignableFrom(derivedColumnType)) {
                if (!String.class.isAssignableFrom(parameterType)) {
                    throwQueryDerivedReferrerUnmatchedColumnTypeException(function, derivedColumnDbName,
                            derivedColumnType);
                }
            }
            if (Number.class.isAssignableFrom(derivedColumnType)) {
                if (!Number.class.isAssignableFrom(parameterType)) {
                    throwQueryDerivedReferrerUnmatchedColumnTypeException(function, derivedColumnDbName,
                            derivedColumnType);
                }
            }
            if (java.util.Date.class.isAssignableFrom(derivedColumnType)) {
                if (!java.util.Date.class.isAssignableFrom(parameterType)) {
                    throwQueryDerivedReferrerUnmatchedColumnTypeException(function, derivedColumnDbName,
                            derivedColumnType);
                }
            }
        }
    }

    protected void throwQueryDerivedReferrerUnmatchedColumnTypeException(String function, String derivedColumnDbName,
            Class<?> derivedColumnType) {
        createCBExThrower().throwQueryDerivedReferrerUnmatchedColumnTypeException(function, derivedColumnDbName,
                derivedColumnType, _value);
    }
}
