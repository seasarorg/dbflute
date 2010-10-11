package org.seasar.dbflute.cbean.chelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.sqlclause.join.FixedConditionResolver;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.DBMetaProvider;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.ForeignInfo;
import org.seasar.dbflute.dbmeta.name.TableSqlName;
import org.seasar.dbflute.exception.DBMetaNotFoundException;
import org.seasar.dbflute.exception.IllegalFixedConditionOverRelationException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.util.Srl;
import org.seasar.dbflute.util.Srl.IndexOfInfo;

/**
 * @author jflute
 * @since 0.9.7.5 (2010/10/11 Monday)
 */
public class HpFixedConditionQueryResolver implements FixedConditionResolver {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final ConditionQuery _localCQ;
    protected final ConditionQuery _foreignCQ;
    protected final DBMetaProvider _dbmetaProvider;
    protected InlineViewResource _inlineViewResource; // internal bridge

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public HpFixedConditionQueryResolver(ConditionQuery localCQ, ConditionQuery foreignCQ, DBMetaProvider dbmetaProvider) {
        _localCQ = localCQ;
        _foreignCQ = foreignCQ;
        _dbmetaProvider = dbmetaProvider;
    }

    // ===================================================================================
    //                                                                    Resolve Variable
    //                                                                    ================
    /**
     * Resolve variables on fixed condition.
     * @param fixedCondition The plain fixed condition. (NotNull: if null, this is not called)
     * @return The resolved fixed condition. (NotNull)
     */
    public String resolveVariable(String fixedCondition) {
        final String localAliasName = _localCQ.xgetAliasName();
        final String foreignAliasName = _foreignCQ.xgetAliasName();
        fixedCondition = replaceString(fixedCondition, "$$alias$$", foreignAliasName); // for compatible
        fixedCondition = replaceString(fixedCondition, getLocalAliasMark(), localAliasName);
        fixedCondition = replaceString(fixedCondition, getForeignAliasMark(), foreignAliasName);
        final String locationBase = _localCQ.xgetLocationBase();
        fixedCondition = replaceString(fixedCondition, getLocationBaseMark() + ".", "pmb." + locationBase);
        fixedCondition = resolveFixedConditionOverRelation(fixedCondition);
        return fixedCondition;
    }

    protected String resolveFixedConditionOverRelation(String fixedCondition) {
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
            final boolean foreign;
            final boolean referrer;
            if (pointTable.equals(getLocalTableMark())) {
                relationPointCQ = _localCQ;
                foreign = false;
                referrer = false;
            } else if (pointTable.equals(getForeignTableMark())) {
                relationPointCQ = _foreignCQ;
                foreign = true;
                referrer = false;
                if (_inlineViewResource == null) {
                    _inlineViewResource = new InlineViewResource();
                }
                String next = remainder.substring(relationEndIndex + relationEndMark.length()).trim();
                if (!next.startsWith(".")) {
                    String notice = "The OverRelation variable should continue to column after the variable.";
                    throwIllegalFixedConditionOverRelationException(notice, pointTable, targetRelation, fixedCondition);
                    return null; // unreachable
                }
                next = next.substring(1);
                final IndexOfInfo indexInfo = Srl.indexOfFirst(next, " ", ",", ")", "\n", "\t", "<", ">", "=", "!");
                next = indexInfo != null ? next.substring(0, indexInfo.getIndex()) : next;
                _inlineViewResource.addAdditionalColumn(next);
                final List<ForeignInfo> nestedJoinList = new ArrayList<ForeignInfo>();
                final List<String> splitList = Srl.splitList(targetRelation, ".");
                DBMeta currentDBMeta = _dbmetaProvider.provideDBMeta(_foreignCQ.getTableDbName());
                for (String element : splitList) {
                    final ForeignInfo foreignInfo = currentDBMeta.findForeignInfo(element);
                    nestedJoinList.add(foreignInfo);
                    currentDBMeta = foreignInfo.getForeignDBMeta();
                }
                _inlineViewResource.addNestedQueryList(targetRelation, nestedJoinList);
            } else {
                final DBMeta pointDBMeta;
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
                foreign = false;
                referrer = true;
            }
            final ConditionQuery columnTargetCQ;
            if (targetRelation != null && !foreign) {
                columnTargetCQ = relationPointCQ.invokeForeignCQ(targetRelation);
            } else {
                if (!referrer && !foreign) {
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
    //                                                            Resolve Fixed InlineView
    //                                                            ========================
    public String resolveFixedInlineView(String foreignTableSqlName) {
        if (_inlineViewResource == null) {
            return foreignTableSqlName; // not uses InlineView
        }
        // alias is required because foreignTableSqlName may be (normal) InlineView
        final String baseAlias = "dffixedbase";
        final StringBuilder sb = new StringBuilder();
        sb.append("(select ").append(baseAlias).append(".*");
        final Set<String> additionalColumnSet = _inlineViewResource.getAdditionalColumnSet();
        for (String columnName : additionalColumnSet) {
            sb.append(", ").append(columnName);
        }
        sb.append(" from ").append(foreignTableSqlName).append(" ").append(baseAlias);

        // may have same relation but 
        final Map<ForeignInfo, String> relationMap = new HashMap<ForeignInfo, String>();
        final Map<String, List<ForeignInfo>> nestedJoinListMap = _inlineViewResource.getNestedJoinListMap();
        int groupIndex = 0;
        for (List<ForeignInfo> nestedJoinList : nestedJoinListMap.values()) {
            final String aliasBase = "dffixedjoin";
            String preForeignAlias = null;
            int joinIndex = 0;
            for (ForeignInfo foreignInfo : nestedJoinList) {
                if (relationMap.containsKey(foreignInfo)) { // already joined
                    preForeignAlias = relationMap.get(foreignInfo); // update previous alias
                    continue;
                }
                final TableSqlName foreignTable;
                final String localAlias;
                final String foreignAlias;
                {
                    final DBMeta foreignDBMeta = foreignInfo.getForeignDBMeta();
                    foreignTable = foreignDBMeta.getTableSqlName();
                    localAlias = (joinIndex == 0 ? baseAlias : preForeignAlias);
                    foreignAlias = aliasBase + "_" + groupIndex + "_" + joinIndex;
                    preForeignAlias = foreignAlias;
                }
                sb.append(" left outer join ").append(foreignTable).append(" ").append(foreignAlias);
                sb.append(" on ");
                final Map<ColumnInfo, ColumnInfo> localForeignColumnInfoMap = foreignInfo
                        .getLocalForeignColumnInfoMap();
                int columnIndex = 0;
                for (Entry<ColumnInfo, ColumnInfo> localForeignEntry : localForeignColumnInfoMap.entrySet()) {
                    final ColumnInfo localColumnInfo = localForeignEntry.getKey();
                    final ColumnInfo foreignColumninfo = localForeignEntry.getValue();
                    if (columnIndex > 0) {
                        sb.append(" and ");
                    }
                    sb.append(localAlias).append(".").append(localColumnInfo.getColumnSqlName());
                    sb.append(" = ").append(foreignAlias).append(".").append(foreignColumninfo.getColumnSqlName());
                    ++columnIndex;
                }
                ++joinIndex;
            }
            ++groupIndex;
        }
        sb.append(")");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                    InlineView Class
    //                                                                    ================
    protected static class InlineViewResource {
        protected Set<String> _additionalColumnSet;
        protected Map<String, List<ForeignInfo>> _nestedJoinListMap;

        public Set<String> getAdditionalColumnSet() {
            return _additionalColumnSet;
        }

        public void addAdditionalColumn(String additionalColumn) {
            if (_additionalColumnSet == null) {
                _additionalColumnSet = new LinkedHashSet<String>();
            }
            _additionalColumnSet.add(additionalColumn);
        }

        public Map<String, List<ForeignInfo>> getNestedJoinListMap() {
            return _nestedJoinListMap;
        }

        public void addNestedQueryList(String relationGroupKey, List<ForeignInfo> nestedJoinList) {
            if (_nestedJoinListMap == null) {
                _nestedJoinListMap = new LinkedHashMap<String, List<ForeignInfo>>();
            }
            _nestedJoinListMap.put(relationGroupKey, nestedJoinList);
        }
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
