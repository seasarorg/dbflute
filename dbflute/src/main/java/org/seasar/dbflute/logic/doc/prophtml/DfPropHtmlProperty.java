/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.logic.doc.prophtml;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 1.0.1 (2012/12/21 Friday)
 */
public class DfPropHtmlProperty {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The key of the property. (NotNull) */
    protected final String _propertyKey;

    /** The (ordered) set of environment type. (NotNull) */
    protected final Set<String> _envTypeSet = DfCollectionUtil.newLinkedHashSet();

    /** The (ordered) set of language type. (NotNull) */
    protected final Set<String> _langTypeSet = DfCollectionUtil.newLinkedHashSet();

    /** The map of environment element that contains language value. map:{envType = map:{langType = value}} (NotNull) */
    protected final Map<String, DfPropHtmlPropertyEnvElement> _envElementMap = DfCollectionUtil.newLinkedHashMap();

    /** Does the property have property comments at least one? */
    protected boolean _hasComment;

    /** Does the property have override elements at least one? */
    protected boolean _hasOverride;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfPropHtmlProperty(String propertyKey) {
        _propertyKey = propertyKey;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getPropertyKey() {
        return _propertyKey;
    }

    public Set<String> getEnvTypeSet() {
        return _envTypeSet;
    }

    public Set<String> getLangTypeSet() {
        return _langTypeSet;
    }

    public DfPropHtmlPropertyEnvElement getEnvElement(String envType) {
        return _envElementMap.get(envType);
    }

    public List<DfPropHtmlPropertyEnvElement> getEnvElementList() {
        return DfCollectionUtil.newArrayList(_envElementMap.values());
    }

    public void setPropertyValue(String envType, String langType, String propertyValue, String comment, boolean override) {
        DfPropHtmlPropertyEnvElement envElement = _envElementMap.get(envType);
        if (envElement == null) {
            envElement = createEnvElement(envType);
            _envElementMap.put(envType, envElement);
        }
        envElement.setPropertyValue(langType, propertyValue, comment, override);
        _envTypeSet.add(envType);
        _langTypeSet.add(langType);
        if (Srl.is_NotNull_and_NotTrimmedEmpty(comment)) {
            _hasComment = true;
        }
        if (override) {
            _hasOverride = true;
        }
    }

    protected DfPropHtmlPropertyEnvElement createEnvElement(String envType) {
        return new DfPropHtmlPropertyEnvElement(_propertyKey, envType);
    }

    public boolean hasComment() {
        return _hasComment;
    }

    public boolean hasOverride() {
        return _hasOverride;
    }

    public boolean isOverride(String envType, String langType) {
        final DfPropHtmlPropertyEnvElement envElement = getEnvElement(envType);
        if (envElement == null) {
            String msg = "Not found the environment element: envType=" + envType;
            throw new IllegalStateException(msg);
        }
        final DfPropHtmlPropertyLangElement langElement = envElement.getLangElement(langType);
        if (langElement == null) {
            String msg = "Not found the language element: envType=" + envType + " langType=" + langType;
            throw new IllegalStateException(msg);
        }
        return langElement.isOverride();
    }
}
