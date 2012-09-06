/*
 * Copyright 2004-2012 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.logic.jdbc.schemadiff;

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.util.DfCollectionUtil;

/**
 * @author awaawa
 * @author jflute
 * @since 0.9.9.8 (2012/09/04 Tuesday)
 */
public class DfCraftDiffTitle extends DfAbstractDiff implements DfNestDiff {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    protected final String _craftTitle;
    protected final List<DfCraftDiffRow> _craftDiffRowAllList = DfCollectionUtil.newArrayList();
    protected final List<DfCraftDiffRow> _addedCraftDiffRowList = DfCollectionUtil.newArrayList();
    protected final List<DfCraftDiffRow> _changedCraftDiffRowList = DfCollectionUtil.newArrayList();
    protected final List<DfCraftDiffRow> _deletedCraftDiffRowList = DfCollectionUtil.newArrayList();

    protected List<NestDiffSetupper> _nestDiffList = DfCollectionUtil.newArrayList();
    {
        _nestDiffList.add(new NestDiffSetupper() {
            public String propertyName() {
                return "craftRowDiff";
            }

            public List<? extends DfNestDiff> provide() {
                return _craftDiffRowAllList;
            }

            public void setup(Map<String, Object> diff) {
                addCraftDiffRow(createCraftRowDiff(diff));
            }
        });
    }

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected DfCraftDiffTitle(String craftTitle) {
        _craftTitle = craftTitle;
    }

    protected DfCraftDiffTitle(Map<String, Object> craftDiffMap) {
        _craftTitle = (String) craftDiffMap.get("craftTitle"); // it's a unique name
        assertCraftKeyNameExists(_craftTitle, craftDiffMap);
        acceptDiffMap(craftDiffMap);
    }

    protected void assertCraftKeyNameExists(String craftKeyName, Map<String, Object> craftDiffMap) {
        if (craftKeyName == null) { // basically no way
            String msg = "The craftKeyName is required in craft diff-map:";
            msg = msg + " craftDiffMap=" + craftDiffMap;
            throw new IllegalStateException(msg);
        }
    }

    public static DfCraftDiffTitle create(String craftTitle) {
        return new DfCraftDiffTitle(craftTitle);
    }

    public static DfCraftDiffTitle createFromDiffMap(Map<String, Object> procedureDiffMap) {
        return new DfCraftDiffTitle(procedureDiffMap);
    }

    // ===================================================================================
    //                                                                            Diff Map
    //                                                                            ========
    public Map<String, Object> createDiffMap() {
        final Map<String, Object> diffMap = DfCollectionUtil.newLinkedHashMap();
        diffMap.put("craftTitle", _craftTitle);
        final List<NestDiffSetupper> nestDiffList = _nestDiffList;
        for (NestDiffSetupper setupper : nestDiffList) {
            final List<? extends DfNestDiff> diffAllList = setupper.provide();
            if (!diffAllList.isEmpty()) {
                final Map<String, Map<String, Object>> nestMap = DfCollectionUtil.newLinkedHashMap();
                for (DfNestDiff nestDiff : diffAllList) {
                    if (nestDiff.hasDiff()) {
                        nestMap.put(nestDiff.getKeyName(), nestDiff.createDiffMap());
                    }
                }
                diffMap.put(setupper.propertyName(), nestMap);
            }
        }
        return diffMap;
    }

    public void acceptDiffMap(Map<String, Object> craftDiffMap) {
        final List<NestDiffSetupper> nestDiffList = _nestDiffList;
        for (NestDiffSetupper setupper : nestDiffList) {
            restoreNestDiff(craftDiffMap, setupper);
        }
    }

    // ===================================================================================
    //                                                                              Status
    //                                                                              ======
    public boolean hasDiff() {
        final List<NestDiffSetupper> nestDiffList = _nestDiffList;
        for (NestDiffSetupper setupper : nestDiffList) {
            final List<? extends DfNestDiff> diffAllList = setupper.provide();
            for (DfNestDiff nestDiff : diffAllList) {
                if (nestDiff.hasDiff()) {
                    return true;
                }
            }
        }
        return false;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    public String getKeyName() { // this 'key' means identity in the DBFlute process
        return getCraftTitle();
    }

    public String getCraftTitle() {
        return _craftTitle != null ? _craftTitle : "";
    }

    public String getLowerCraftTitle() {
        return getCraftTitle().toLowerCase();
    }

    public String getCraftDispTitle() {
        return getCraftTitle();
    }

    public DfDiffType getDiffType() { // basically unused
        return DfDiffType.CHANGE; // fixed
    }

    // -----------------------------------------------------
    //                                             Diff Item
    //                                             ---------
    public List<NextPreviousHandler> getNextPreviousDiffList() {
        return DfCollectionUtil.emptyList();
    }

    // -----------------------------------------------------
    //                                             CraftDiff
    //                                             ---------
    public List<DfCraftDiffRow> getCraftDiffRowAllList() {
        return _craftDiffRowAllList;
    }

    public List<DfCraftDiffRow> getAddedCraftRowDiffList() {
        return _addedCraftDiffRowList;
    }

    public List<DfCraftDiffRow> getChangedCraftRowDiffList() {
        return _changedCraftDiffRowList;
    }

    public List<DfCraftDiffRow> getDeletedCraftRowDiffList() {
        return _deletedCraftDiffRowList;
    }

    public void addCraftDiffRow(DfCraftDiffRow craftDiffRow) {
        _craftDiffRowAllList.add(craftDiffRow);
        if (craftDiffRow.isAdded()) {
            _addedCraftDiffRowList.add(craftDiffRow);
        } else if (craftDiffRow.isChanged()) {
            _changedCraftDiffRowList.add(craftDiffRow);
        } else if (craftDiffRow.isDeleted()) {
            _deletedCraftDiffRowList.add(craftDiffRow);
        } else { // no way
            String msg = "Unknown diff-type of craft: ";
            msg = msg + " diffType=" + craftDiffRow.getDiffType() + " craftDiffRow=" + craftDiffRow;
            throw new IllegalStateException(msg);
        }
    }
}
