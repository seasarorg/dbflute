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
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.seasar.dbflute.helper.token.file.FileToken;
import org.seasar.dbflute.helper.token.file.FileTokenizingCallback;
import org.seasar.dbflute.helper.token.file.FileTokenizingOption;
import org.seasar.dbflute.helper.token.file.FileTokenizingRowResource;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.9.8 (2012/09/04 Tuesday)
 */
public class DfCraftDiffProcess extends DfAbstractDiff {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final Set<String> _craftTitleSet = DfCollectionUtil.newLinkedHashSet();
    protected final Map<String, List<DfCraftDiff>> _craftDiffAllList = DfCollectionUtil.newLinkedHashMap();
    protected final Map<String, List<DfCraftDiff>> _addedCraftDiffList = DfCollectionUtil.newLinkedHashMap();
    protected final Map<String, List<DfCraftDiff>> _changedCraftDiffList = DfCollectionUtil.newLinkedHashMap();
    protected final Map<String, List<DfCraftDiff>> _deletedCraftDiffList = DfCollectionUtil.newLinkedHashMap();

    // map:{titleName = map:{craftKey : meta}}
    protected Map<String, Map<String, DfCraftDiffMeta>> _nextCraftMetaMap;
    protected Map<String, Map<String, DfCraftDiffMeta>> _previousCraftMetaMap;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfCraftDiffProcess() {
    }

    // ===================================================================================
    //                                                                   CraftDiff Process
    //                                                                   =================
    public void craftDiff(String metaDirPath) {
        loadMeta(metaDirPath);
        if (!existsCraftMeta()) {
            return;
        }
        processAddedCraft();
        processChangedCraft();
        processDeletedCraft();
    }

    protected boolean existsCraftMeta() {
        return _nextCraftMetaMap != null && _previousCraftMetaMap != null;
    }

    // ===================================================================================
    //                                                                     Loading Process
    //                                                                     ===============
    protected void loadMeta(String metaDirPath) {
        final File metaDir = new File(metaDirPath);
        if (!metaDir.exists() || !metaDir.isDirectory()) {
            return;
        }
        final String filePrefix = "craft-diff-";
        final String fileExt = ".tsv";
        final File[] listFiles = metaDir.listFiles(new FilenameFilter() {
            public boolean accept(File file, String name) {
                if (file.isDirectory()) {
                    return false;
                }
                return name.startsWith(filePrefix) && name.endsWith(fileExt);
            }
        });
        if (listFiles == null || listFiles.length == 0) { // empty directory
            return;
        }
        for (final File tsvFile : listFiles) {
            final String pureName = tsvFile.getName();
            final String resourceName = Srl.extractScopeWide(pureName, filePrefix, fileExt).getContent();
            if (!resourceName.contains("-")) {
                // TODO jflute
            }
            final String craftTitle = Srl.substringLastFront(resourceName, "-");
            _craftTitleSet.add(craftTitle);
            final boolean next;
            {
                final String nextPrevious = Srl.substringLastRear(resourceName, "-");
                if (nextPrevious.equalsIgnoreCase("next")) {
                    next = true;
                } else if (nextPrevious.equalsIgnoreCase("previous")) {
                    next = false;
                } else {
                    // TODO jflute
                    throw new IllegalStateException();
                }
            }
            final FileToken fileToken = new FileToken();
            final List<DfCraftDiffMeta> nextMetaList = DfCollectionUtil.newArrayList();
            final List<DfCraftDiffMeta> previousMetaList = DfCollectionUtil.newArrayList();
            try {
                fileToken.tokenize(new FileInputStream(tsvFile), new FileTokenizingCallback() {
                    public void handleRowResource(FileTokenizingRowResource rowResource) {
                        final List<String> craftValueList = DfCollectionUtil.newArrayList(rowResource.getValueList());
                        final String craftKeyName = craftValueList.remove(0); // it's the first item fixedly
                        final DfCraftDiffMeta meta = new DfCraftDiffMeta(craftKeyName, buildCraftValue(craftValueList));
                        if (next) {
                            nextMetaList.add(meta);
                        } else {
                            previousMetaList.add(meta);
                        }
                    }
                }, new FileTokenizingOption().delimitateByTab().encodeAsUTF8().handleEmptyAsNull());
            } catch (IOException e) {
                String msg = "Failed to read the file: " + tsvFile;
                throw new IllegalStateException(msg, e);
            }
            registerNextMeta(craftTitle, nextMetaList);
            registerPreviousMeta(craftTitle, previousMetaList);
        }
        // TODO jflute check
    }

    protected void registerNextMeta(String craftTitle, List<DfCraftDiffMeta> metaList) {
        if (_nextCraftMetaMap == null) {
            _nextCraftMetaMap = DfCollectionUtil.newLinkedHashMap();
        }
        doRegisterMeta(craftTitle, metaList, _nextCraftMetaMap);
    }

    protected void registerPreviousMeta(String craftTitle, List<DfCraftDiffMeta> metaList) {
        if (_previousCraftMetaMap == null) {
            _previousCraftMetaMap = DfCollectionUtil.newLinkedHashMap();
        }
        doRegisterMeta(craftTitle, metaList, _previousCraftMetaMap);
    }

    protected void doRegisterMeta(String craftTitle, List<DfCraftDiffMeta> metaList,
            Map<String, Map<String, DfCraftDiffMeta>> titleMetaMap) {
        Map<String, DfCraftDiffMeta> metaMap = titleMetaMap.get(craftTitle);
        if (metaMap == null) {
            metaMap = DfCollectionUtil.newLinkedHashMap();
            titleMetaMap.put(craftTitle, metaMap);
        }
        for (DfCraftDiffMeta meta : metaList) {
            metaMap.put(meta.getCraftKeyName(), meta);
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
    //                                                                   Procedure Process
    //                                                                   =================
    // -----------------------------------------------------
    //                                                 Added
    //                                                 -----
    protected void processAddedCraft() {
        for (Entry<String, Map<String, DfCraftDiffMeta>> titleEntry : _nextCraftMetaMap.entrySet()) {
            final String craftTitle = titleEntry.getKey();
            if (!hasPreviousCraftDiffMeta(craftTitle)) {
                continue; // means no meta at previous time
            }
            final Map<String, DfCraftDiffMeta> elementMap = titleEntry.getValue();
            for (Entry<String, DfCraftDiffMeta> entry : elementMap.entrySet()) {
                final DfCraftDiffMeta meta = entry.getValue();
                final DfCraftDiffMeta found = findPreviousCraftDiffData(craftTitle, meta);
                if (found == null || !isSameCraftKeyName(meta, found)) { // added
                    addCraftDiff(craftTitle, DfCraftDiff.createAdded(meta.getCraftKeyName()));
                }
            }
        }
    }

    // -----------------------------------------------------
    //                                               Changed
    //                                               -------
    protected void processChangedCraft() {
        for (Entry<String, Map<String, DfCraftDiffMeta>> titleEntry : _nextCraftMetaMap.entrySet()) {
            final String craftTitle = titleEntry.getKey();
            final Map<String, DfCraftDiffMeta> elementMap = titleEntry.getValue();
            for (Entry<String, DfCraftDiffMeta> entry : elementMap.entrySet()) {
                final DfCraftDiffMeta next = entry.getValue();
                final DfCraftDiffMeta previous = findPreviousCraftDiffData(craftTitle, next);
                if (previous == null || !isSameCraftKeyName(next, previous)) {
                    continue;
                }
                // found
                final DfCraftDiff craftDiff = DfCraftDiff.createChanged(next.getCraftKeyName());

                // only one item
                processCraftValue(next, previous, craftDiff);

                if (craftDiff.hasDiff()) { // changed
                    addCraftDiff(craftTitle, craftDiff);
                }
            }
        }
    }

    protected void processCraftValue(DfCraftDiffMeta next, DfCraftDiffMeta previous, DfCraftDiff craftDiff) {
        diffNextPrevious(next, previous, craftDiff, new StringNextPreviousDiffer<DfCraftDiffMeta, DfCraftDiff>() {
            public String provide(DfCraftDiffMeta obj) {
                return obj.getCraftValue();
            }

            public void diff(DfCraftDiff diff, DfNextPreviousDiff nextPreviousDiff) {
                diff.setCraftValueDiff(nextPreviousDiff);
            }
        });
    }

    protected <TYPE> void diffNextPrevious(DfCraftDiffMeta next, DfCraftDiffMeta previous, DfCraftDiff diff,
            NextPreviousDiffer<DfCraftDiffMeta, DfCraftDiff, TYPE> differ) {
        final TYPE nextValue = differ.provide(next);
        final TYPE previousValue = differ.provide(previous);
        if (!differ.isMatch(nextValue, previousValue)) {
            final String nextDisp = differ.disp(nextValue, true);
            final String previousDisp = differ.disp(previousValue, false);
            differ.diff(diff, createNextPreviousDiff(nextDisp, previousDisp));
        }
    }

    // -----------------------------------------------------
    //                                               Deleted
    //                                               -------
    protected void processDeletedCraft() {
        for (Entry<String, Map<String, DfCraftDiffMeta>> titleEntry : _previousCraftMetaMap.entrySet()) {
            final String craftTitle = titleEntry.getKey();
            if (!hasNextCraftDiffMeta(craftTitle)) {
                continue; // means no meta at next time
            }
            final Map<String, DfCraftDiffMeta> elementMap = titleEntry.getValue();
            for (Entry<String, DfCraftDiffMeta> entry : elementMap.entrySet()) {
                final DfCraftDiffMeta meta = entry.getValue();
                final DfCraftDiffMeta found = findNextCraftDiffData(craftTitle, meta);
                if (found == null || !isSameCraftKeyName(meta, found)) { // deleted
                    addCraftDiff(craftTitle, DfCraftDiff.createDeleted(meta.getCraftKeyName()));
                }
            }
        }
    }

    // -----------------------------------------------------
    //                                         Assist Helper
    //                                         -------------
    protected boolean isSameCraftKeyName(DfCraftDiffMeta next, DfCraftDiffMeta previous) {
        return isSame(next.getCraftKeyName(), previous.getCraftKeyName());
    }

    // ===================================================================================
    //                                                                         Find Object
    //                                                                         ===========
    protected boolean hasNextCraftDiffMeta(String craftTitle) {
        return _nextCraftMetaMap.get(craftTitle) != null;
    }

    protected DfCraftDiffMeta findNextCraftDiffData(String craftTitle, DfCraftDiffMeta meta) {
        final Map<String, DfCraftDiffMeta> metaMap = _nextCraftMetaMap.get(craftTitle);
        return metaMap != null ? metaMap.get(meta.getCraftKeyName()) : null;
    }

    protected boolean hasPreviousCraftDiffMeta(String craftTitle) {
        return _previousCraftMetaMap.get(craftTitle) != null;
    }

    protected DfCraftDiffMeta findPreviousCraftDiffData(String craftTitle, DfCraftDiffMeta meta) {
        final Map<String, DfCraftDiffMeta> metaMap = _previousCraftMetaMap.get(craftTitle);
        return metaMap != null ? metaMap.get(meta.getCraftKeyName()) : null;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public List<String> getCraftTitleList() {
        return DfCollectionUtil.newArrayList(_craftTitleSet);
    }

    public List<DfCraftDiff> getCraftDiffAllList(String craftTitle) {
        return _craftDiffAllList.get(craftTitle);
    }

    public List<DfCraftDiff> getAddedCraftDiffList(String craftTitle) {
        return _addedCraftDiffList.get(craftTitle);
    }

    public List<DfCraftDiff> getChangedCraftDiffList(String craftTitle) {
        return _changedCraftDiffList.get(craftTitle);
    }

    public List<DfCraftDiff> getDeletedCraftDiffList(String craftTitle) {
        return _deletedCraftDiffList.get(craftTitle);
    }

    public void addCraftDiff(String craftTitle, DfCraftDiff craftDiff) {
        doAddCraftDiff(craftTitle, craftDiff, _craftDiffAllList);
        if (craftDiff.isAdded()) {
            doAddCraftDiff(craftTitle, craftDiff, _addedCraftDiffList);
        } else if (craftDiff.isChanged()) {
            doAddCraftDiff(craftTitle, craftDiff, _changedCraftDiffList);
        } else if (craftDiff.isDeleted()) {
            doAddCraftDiff(craftTitle, craftDiff, _deletedCraftDiffList);
        } else { // no way
            String msg = "Unknown diff-type of craft: ";
            msg = msg + " diffType=" + craftDiff.getDiffType() + " craftDiff=" + craftDiff;
            throw new IllegalStateException(msg);
        }
    }

    protected void doAddCraftDiff(String craftTitle, DfCraftDiff craftDiff, Map<String, List<DfCraftDiff>> craftDiffMap) {
        List<DfCraftDiff> craftDiffList = craftDiffMap.get(craftTitle);
        if (craftDiffList == null) {
            craftDiffList = DfCollectionUtil.newArrayList();
            craftDiffMap.put(craftTitle, craftDiffList);
        }
        craftDiffList.add(craftDiff);
    }
}
