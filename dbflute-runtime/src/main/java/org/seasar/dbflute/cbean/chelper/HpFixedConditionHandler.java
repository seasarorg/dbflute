package org.seasar.dbflute.cbean.chelper;

import java.util.Map;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.DBMetaProvider;
import org.seasar.dbflute.dbmeta.name.ColumnRealName;
import org.seasar.dbflute.exception.DBMetaNotFoundException;
import org.seasar.dbflute.exception.IllegalFixedConditionOverRelationException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.7.5 (2010/10/11 Monday)
 */
public class HpFixedConditionHandler {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final ConditionQuery _localCQ;
    protected final DBMetaProvider _dbmetaProvider;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public HpFixedConditionHandler(ConditionQuery localCQ, DBMetaProvider dbmetaProvider) {
        _localCQ = localCQ;
        _dbmetaProvider = dbmetaProvider;
    }

    // ===================================================================================
    //                                                                             Resolve
    //                                                                             =======
    /**
     * Resolve variables on fixed condition.
     * @param foreignCQ The condition-query for foreign table. (NotNull)
     * @param joinOnMap The map of join-on clause. (NotNull, ReadOnly)
     * @param fixedCondition The plain fixed condition. (NotNull: if null, this is not called)
     * @return The resolved fixed condition. (NotNull)
     */
    public String resolveVariable(ConditionQuery foreignCQ, Map<ColumnRealName, ColumnRealName> joinOnMap,
            String fixedCondition) {
        final String localAliasName = _localCQ.xgetAliasName();
        final String foreignAliasName = foreignCQ.xgetAliasName();
        fixedCondition = replaceString(fixedCondition, "$$alias$$", foreignAliasName); // for compatible
        fixedCondition = replaceString(fixedCondition, getLocalAliasMark(), localAliasName);
        fixedCondition = replaceString(fixedCondition, getForeignAliasMark(), foreignAliasName);
        final String locationBase = _localCQ.xgetLocationBase();
        fixedCondition = replaceString(fixedCondition, getLocationBaseMark() + ".", "pmb." + locationBase);
        fixedCondition = resolveFixedConditionOverRelation(foreignCQ, joinOnMap, fixedCondition);
        return fixedCondition;
    }

    protected String resolveFixedConditionOverRelation(ConditionQuery foreignCQ,
            Map<ColumnRealName, ColumnRealName> joinOnMap, String fixedCondition) {
        final String relationBeginMark = getRelationBeginMark();
        final String relationEndMark = getRelationEndMark();
        String remainder = fixedCondition;
        while (true) {
            final int relationBeginIndex = remainder.indexOf(relationBeginMark);
            if (relationBeginIndex < 0) {
                break;
            }
            remainder = remainder.substring(relationBeginIndex + relationBeginMark.length());
            final int relationEndIndex = remainder.indexOf(relationEndMark);
            if (relationEndIndex < 0) {
                break;
            }
            final String relationExp = remainder.substring(0, relationEndIndex);
            final int separatorIndex = relationExp.indexOf(".");
            final String pointTable;
            final String targetRelation;
            if (separatorIndex >= 0) {
                pointTable = relationExp.substring(0, separatorIndex).trim();
                targetRelation = relationExp.substring(separatorIndex + ".".length()).trim();
            } else {
                pointTable = relationExp.trim();
                targetRelation = null;
            }
            final ConditionQuery relationPointCQ;
            final boolean referrer;
            final DBMeta pointDBMeta;
            if (pointTable.equals(getLocalTableMark())) {
                relationPointCQ = _localCQ;
                referrer = false;
            } else if (pointTable.equals(getForeignTableMark())) {
                relationPointCQ = foreignCQ;
                referrer = false;
            } else {
                try {
                    pointDBMeta = _dbmetaProvider.provideDBMeta(pointTable);
                } catch (DBMetaNotFoundException e) {
                    String notice = "The table for relation on fixed condition does not exist.";
                    throwIllegalFixedConditionOverRelationException(notice, pointTable, targetRelation, fixedCondition,
                            e);
                    return null; // unreachable
                }
                ConditionQuery referrerQuery = _localCQ.xgetReferrerQuery();
                while (true) {
                    if (referrerQuery == null) { // means not found
                        break;
                    }
                    if (pointDBMeta.getTableDbName().equals(referrerQuery.getTableDbName())) {
                        break;
                    }
                    referrerQuery = referrerQuery.xgetReferrerQuery();
                }
                relationPointCQ = referrerQuery;
                if (relationPointCQ == null) {
                    String notice = "The table for relation on fixed condition was not found in the scope.";
                    throwIllegalFixedConditionOverRelationException(notice, pointTable, targetRelation, fixedCondition);
                    return null; // unreachable
                }
                referrer = true;
            }
            final ConditionQuery columnTargetCQ;
            if (targetRelation != null) {
                columnTargetCQ = relationPointCQ.invokeForeignCQ(targetRelation);
            } else {
                if (!referrer) {
                    String notice = "The relation on fixed condition is required if the table is referrer.";
                    throwIllegalFixedConditionOverRelationException(notice, pointTable, null, fixedCondition);
                }
                columnTargetCQ = relationPointCQ;
            }
            final String relationVariable = relationBeginMark + relationExp + relationEndMark;
            final String relationAlias = columnTargetCQ.xgetAliasName();
            fixedCondition = replaceString(fixedCondition, relationVariable, relationAlias);

            // after case for loop
            remainder = remainder.substring(relationEndIndex + relationEndMark.length());

            // for prevent from processing same one
            remainder = replaceString(remainder, relationVariable, relationAlias);
        }
        return fixedCondition;
    }

    // ===================================================================================
    //                                                                  Exception Handling
    //                                                                  ==================
    protected void throwIllegalFixedConditionOverRelationException(String notice, String tableName,
            String relationName, String fixedCondition) {
        throwIllegalFixedConditionOverRelationException(notice, tableName, relationName, fixedCondition, null);
    }

    protected void throwIllegalFixedConditionOverRelationException(String notice, String pointTable,
            String targetRelation, String fixedCondition, Exception e) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice(notice);
        br.addItem("Point Table");
        br.addElement(pointTable);
        br.addItem("Target Relation");
        br.addElement(targetRelation);
        br.addItem("Fixed Condition");
        br.addElement(fixedCondition);
        br.addItem("BizOneToOne's Local");
        br.addElement(_localCQ.getTableDbName());
        final String msg = br.buildExceptionMessage();
        throw new IllegalFixedConditionOverRelationException(msg, e);
    }

    // ===================================================================================
    //                                                                       Variable Mark
    //                                                                       =============
    protected String getLocalAliasMark() {
        return "$$localAlias$$";
    }

    protected String getForeignAliasMark() {
        return "$$foreignAlias$$";
    }

    protected String getLocationBaseMark() {
        return "$$locationBase$$";
    }

    protected String getRelationBeginMark() {
        return "$$over(";
    }

    protected String getRelationEndMark() {
        return ")$$";
    }

    protected String getLocalTableMark() {
        return "!localTable!";
    }

    protected String getForeignTableMark() {
        return "!foreignTable!";
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected final String replaceString(String text, String fromText, String toText) {
        return Srl.replace(text, fromText, toText);
    }
}
