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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.token.file.FileToken;
import org.seasar.dbflute.helper.token.file.FileTokenizingCallback;
import org.seasar.dbflute.helper.token.file.FileTokenizingOption;
import org.seasar.dbflute.helper.token.file.FileTokenizingRowResource;
import org.seasar.dbflute.properties.DfDocumentProperties;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.9.8 (2012/09/04 Tuesday)
 */
public class DfCraftDiff extends DfAbstractDiff {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final Set<String> _craftTitleSet = DfCollectionUtil.newLinkedHashSet();
    protected final List<DfCraftDiffTitle> _craftDiffTitleList = DfCollectionUtil.newArrayList();

    // map:{craftTitle = map:{craftKey : craftMeta}}
    protected Map<String, Map<String, DfCraftValue>> _nextTitleCraftValueMap;
    protected Map<String, Map<String, DfCraftValue>> _previousTitleCraftValueMap;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfCraftDiff() {
    }

    protected static class DfCraftValue {
        protected final String _craftKeyName;
        protected final String _craftValue;

        public DfCraftValue(String craftKeyName, String craftValue) {
            _craftKeyName = craftKeyName;
            _craftValue = craftValue;
        }

        public String getCraftKeyName() {
            return _craftKeyName;
        }

        public String getCraftValue() {
            return _craftValue;
        }
    }

    // ===================================================================================
    //                                                                        Analyze Diff
    //                                                                        ============
    public void analyzeDiff(String metaDirPath) {
        loadMeta(metaDirPath);
        if (!existsCraftMeta()) {
            return;
        }
        processCraftDiff();
    }

    protected boolean existsCraftMeta() {
        return _nextTitleCraftValueMap != null && _previousTitleCraftValueMap != null;
    }

    // ===================================================================================
    //                                                                     Loading Process
    //                                                                     ===============
    protected void loadMeta(String craftMetaDir) {
        final File metaDir = new File(craftMetaDir);
        if (!metaDir.exists() || !metaDir.isDirectory()) {
            return;
        }
        final List<File> metaFileList = getCraftMetaFileList(craftMetaDir);
        if (metaFileList.isEmpty()) { // empty directory or no check
            return;
        }
        for (final File metaFile : metaFileList) {
            final String craftTitle = extractCraftTitle(metaFile);
            _craftTitleSet.add(craftTitle);
            final boolean next = isCraftDirectionNext(metaFile);
            final FileToken fileToken = new FileToken();
            final List<DfCraftValue> valueList = DfCollectionUtil.newArrayList();
            try {
                fileToken.tokenize(new FileInputStream(metaFile), new FileTokenizingCallback() {
                    public void handleRowResource(FileTokenizingRowResource rowResource) {
                        final List<String> craftValueList = DfCollectionUtil.newArrayList(rowResource.getValueList());
                        final String craftKeyName = craftValueList.remove(0); // it's the first item fixedly
                        valueList.add(new DfCraftValue(craftKeyName, buildCraftValue(craftValueList)));
                    }
                }, new FileTokenizingOption().delimitateByTab().encodeAsUTF8().handleEmptyAsNull());
            } catch (IOException e) {
                String msg = "Failed to read the file: " + metaFile;
                throw new IllegalStateException(msg, e);
            }
            if (next) {
                registerNextMeta(craftTitle, valueList);
            } else {
                registerPreviousMeta(craftTitle, valueList);
            }
        }
    }

    protected void registerNextMeta(String craftTitle, List<DfCraftValue> valueList) {
        if (_nextTitleCraftValueMap == null) {
            _nextTitleCraftValueMap = DfCollectionUtil.newLinkedHashMap();
        }
        doRegisterMeta(craftTitle, valueList, _nextTitleCraftValueMap);
    }

    protected void registerPreviousMeta(String craftTitle, List<DfCraftValue> valueList) {
        if (_previousTitleCraftValueMap == null) {
            _previousTitleCraftValueMap = DfCollectionUtil.newLinkedHashMap();
        }
        doRegisterMeta(craftTitle, valueList, _previousTitleCraftValueMap);
    }

    protected void doRegisterMeta(String craftTitle, List<DfCraftValue> craftValueList,
            Map<String, Map<String, DfCraftValue>> titleValueMap) {
        Map<String, DfCraftValue> valueMap = titleValueMap.get(craftTitle);
        if (valueMap == null) {
            valueMap = DfCollectionUtil.newLinkedHashMap();
            titleValueMap.put(craftTitle, valueMap);
        }
        for (DfCraftValue craftValue : craftValueList) {
            valueMap.put(craftValue.getCraftKeyName(), craftValue);
        }
    }

    protected String buildCraftValue(final List<String> craftValueList) {
        final StringBuilder sb = new StringBuilder();
        for (String craftValue : craftValueList) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append(craftValue);
        }
        return sb.toString();
    }

    // ===================================================================================
    //                                                                   CraftDiff Process
    //                                                                   =================
    protected void processCraftDiff() {
        for (Entry<String, Map<String, DfCraftValue>> entry : _nextTitleCraftValueMap.entrySet()) {
            final String craftTitle = entry.getKey();
            final DfCraftDiffTitle titleDiff = DfCraftDiffTitle.create(craftTitle);
            if (!hasPreviousCraftValue(titleDiff)) { // means no meta at previous time
                continue; // out of target
            }
            addCraftDiffTitle(titleDiff);
            doProcessCraftDiff(titleDiff);
        }
    }

    protected void doProcessCraftDiff(DfCraftDiffTitle titleDiff) {
        processAddedCraft(titleDiff);
        processChangedCraft(titleDiff);
        processDeletedCraft(titleDiff);
    }

    // -----------------------------------------------------
    //                                                 Added
    //                                                 -----
    protected void processAddedCraft(DfCraftDiffTitle titleDiff) {
        final Map<String, DfCraftValue> nextValueMap = findNextValueMap(titleDiff); // exists here
        for (Entry<String, DfCraftValue> entry : nextValueMap.entrySet()) {
            final DfCraftValue craftValue = entry.getValue();
            final DfCraftValue found = findPreviousCraftValue(titleDiff, craftValue);
            if (found == null || !isSameCraftKeyName(craftValue, found)) { // added
                titleDiff.addCraftDiffRow(DfCraftDiffRow.createAdded(craftValue.getCraftKeyName()));
            }
        }
    }

    protected Map<String, DfCraftValue> findNextValueMap(DfCraftDiffTitle titleDiff) {
        final String craftTitle = titleDiff.getKeyName();
        return _nextTitleCraftValueMap.get(craftTitle); // always exists here
    }

    // -----------------------------------------------------
    //                                               Changed
    //                                               -------
    protected void processChangedCraft(DfCraftDiffTitle titleDiff) {
        final Map<String, DfCraftValue> nextValueMap = findNextValueMap(titleDiff); // exists here
        for (Entry<String, DfCraftValue> entry : nextValueMap.entrySet()) {
            final DfCraftValue next = entry.getValue();
            final DfCraftValue previous = findPreviousCraftValue(titleDiff, next);
            if (previous == null || !isSameCraftKeyName(next, previous)) {
                continue;
            }
            // found
            final DfCraftDiffRow craftDiff = DfCraftDiffRow.createChanged(next.getCraftKeyName());

            // only one item
            processCraftValue(next, previous, craftDiff);

            if (craftDiff.hasDiff()) { // changed
                titleDiff.addCraftDiffRow(craftDiff);
            }
        }
    }

    protected void processCraftValue(DfCraftValue next, DfCraftValue previous, DfCraftDiffRow craftDiff) {
        diffNextPrevious(next, previous, craftDiff, new StringNextPreviousDiffer<DfCraftValue, DfCraftDiffRow>() {
            public String provide(DfCraftValue obj) {
                return obj.getCraftValue();
            }

            public void diff(DfCraftDiffRow diff, DfNextPreviousDiff nextPreviousDiff) {
                diff.setCraftValueDiff(nextPreviousDiff);
            }
        });
    }

    protected void diffNextPrevious(DfCraftValue next, DfCraftValue previous, DfCraftDiffRow diff,
            StringNextPreviousDiffer<DfCraftValue, DfCraftDiffRow> differ) {
        final String nextValue = differ.provide(next);
        final String previousValue = differ.provide(previous);
        final String nextDiffValue;
        final String previousDiffValue;
        if (needsToHash(nextValue, previousValue)) {
            nextDiffValue = nextValue != null ? convertToHash(nextValue) : null;
            previousDiffValue = previousValue != null ? convertToHash(previousValue) : null;
        } else {
            nextDiffValue = nextValue;
            previousDiffValue = previousValue;
        }
        doDiffNextPrevious(diff, differ, nextDiffValue, previousDiffValue);
    }

    protected boolean needsToHash(String nextValue, String previousValue) {
        return containsLineSeparator(nextValue) || containsLineSeparator(previousValue); // either contains
    }

    protected boolean containsLineSeparator(String value) {
        return value != null && value.contains("\n");
    }

    protected String convertToHash(String value) {
        final StringBuilder nextSb = new StringBuilder();
        nextSb.append(Srl.count(value, "\n") + 1).append(":"); // line
        nextSb.append(value.length()).append(":"); // length
        nextSb.append(Integer.toHexString(value.hashCode())); // hash
        return nextSb.toString();
    }

    protected void doDiffNextPrevious(DfCraftDiffRow diff,
            StringNextPreviousDiffer<DfCraftValue, DfCraftDiffRow> differ, final String nextValue,
            final String previousValue) {
        if (!differ.isMatch(nextValue, previousValue)) {
            final String nextDisp = differ.disp(nextValue, true);
            final String previousDisp = differ.disp(previousValue, false);
            differ.diff(diff, createNextPreviousDiff(nextDisp, previousDisp));
        }
    }

    // -----------------------------------------------------
    //                                               Deleted
    //                                               -------
    protected void processDeletedCraft(DfCraftDiffTitle titleDiff) {
        final Map<String, DfCraftValue> previousValueMap = findPreviousCraftValueMap(titleDiff); // exists here
        for (Entry<String, DfCraftValue> entry : previousValueMap.entrySet()) {
            final DfCraftValue craftValue = entry.getValue();
            final DfCraftValue found = findNextCraftDiffData(titleDiff, craftValue);
            if (found == null || !isSameCraftKeyName(craftValue, found)) { // deleted
                titleDiff.addCraftDiffRow(DfCraftDiffRow.createDeleted(craftValue.getCraftKeyName()));
            }
        }
    }

    // -----------------------------------------------------
    //                                         Assist Helper
    //                                         -------------
    protected boolean isSameCraftKeyName(DfCraftValue next, DfCraftValue previous) {
        return isSame(next.getCraftKeyName(), previous.getCraftKeyName());
    }

    // ===================================================================================
    //                                                                         Find Object
    //                                                                         ===========
    // -----------------------------------------------------
    //                                                  Next
    //                                                  ----
    protected boolean hasNextCraftValue(DfCraftDiffTitle titleDiff) {
        return findNextCraftValueMap(titleDiff) != null;
    }

    protected DfCraftValue findNextCraftDiffData(DfCraftDiffTitle titleDiff, DfCraftValue craftValue) {
        final Map<String, DfCraftValue> metaMap = _nextTitleCraftValueMap.get(titleDiff.getKeyName());
        return metaMap != null ? metaMap.get(craftValue.getCraftKeyName()) : null;
    }

    protected Map<String, DfCraftValue> findNextCraftValueMap(DfCraftDiffTitle titleDiff) {
        return _nextTitleCraftValueMap.get(titleDiff.getKeyName());
    }

    // -----------------------------------------------------
    //                                              Previous
    //                                              --------
    protected boolean hasPreviousCraftValue(DfCraftDiffTitle titleDiff) {
        return findPreviousCraftValueMap(titleDiff) != null;
    }

    protected DfCraftValue findPreviousCraftValue(DfCraftDiffTitle titleDiff, DfCraftValue craftValue) {
        final Map<String, DfCraftValue> metaMap = _previousTitleCraftValueMap.get(titleDiff.getKeyName());
        return metaMap != null ? metaMap.get(craftValue.getCraftKeyName()) : null;
    }

    protected Map<String, DfCraftValue> findPreviousCraftValueMap(DfCraftDiffTitle titleDiff) {
        return _previousTitleCraftValueMap.get(titleDiff.getKeyName());
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    protected DfDocumentProperties getDocumentProperties() {
        return getProperties().getDocumentProperties();
    }

    protected List<File> getCraftMetaFileList(String craftMetaDir) {
        return getDocumentProperties().getCraftMetaFileList(craftMetaDir);
    }

    protected String extractCraftTitle(File metaFile) {
        return getDocumentProperties().extractCraftTitle(metaFile);
    }

    protected boolean isCraftDirectionNext(File metaFile) {
        return getDocumentProperties().isCraftDirectionNext(metaFile);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public List<DfCraftDiffTitle> getCraftDiffTitleList() {
        return _craftDiffTitleList;
    }

    public void addCraftDiffTitle(DfCraftDiffTitle craftDiffTitle) {
        _craftDiffTitleList.add(craftDiffTitle);
    }
}
