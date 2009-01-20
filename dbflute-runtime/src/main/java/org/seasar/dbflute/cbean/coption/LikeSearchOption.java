package org.seasar.dbflute.cbean.coption;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.cbean.coption.parts.local.JapaneseOptionPartsAgent;
import org.seasar.dbflute.resource.ResourceContext;


/**
 * The class of like-search-option.
 * @author jflute
 */
public class LikeSearchOption extends SimpleStringOption {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final String LIKE_PREFIX = "prefix";
    protected static final String LIKE_SUFFIX = "suffix";
    protected static final String LIKE_CONTAIN = "contain";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _like;
    protected String _escape;
    protected boolean _asOrSplit;
    protected List<LikeAsOrCallback> _likeAsOrCallbackList;

    // ===================================================================================
    //                                                                         Rear Option
    //                                                                         ===========
    public String getRearOption() {
        if (_escape == null || _escape.trim().length() == 0) {
            return "";
        }
        return " escape '" + _escape + "'";
    }

    // ===================================================================================
    //                                                                                AsOr
    //                                                                                ====
    /** @deprecated */
    public static interface LikeAsOrCallback {
        public String getAdditionalTargetPropertyName();
        public String filterValue(String currentValue);
        public LikeSearchOption filterOption(LikeSearchOption optionDeepCopyWithoutCallback);
    }

	/** @deprecated */
    public static abstract class DefaultLikeAsOrCallback implements LikeAsOrCallback {
        public String filterValue(String currentValue) {
            return currentValue;
        }
        public LikeSearchOption filterOption(LikeSearchOption optionDeepCopyWithoutCallback) {
            return optionDeepCopyWithoutCallback;
        }
    }

    public boolean hasLikeAsOrCallback() {
        return _likeAsOrCallbackList != null && !_likeAsOrCallbackList.isEmpty();
    }
	
    public List<LikeAsOrCallback> getLikeAsOrCallbackList() {
		if (_likeAsOrCallbackList == null) {
		    _likeAsOrCallbackList = new ArrayList<LikeAsOrCallback>();
		}
        return _likeAsOrCallbackList;
    }

	/** 
     * @param likeAsOrCallback Callback.
     * @deprecated
     */
    public void addLikeAsOrCallback(LikeAsOrCallback likeAsOrCallback) {
        getLikeAsOrCallbackList().add(likeAsOrCallback);
    }

    public void clearLikeAsOrCallback() {
        getLikeAsOrCallbackList().clear();
    }

    // ===================================================================================
    //                                                                                Like
    //                                                                                ====
    public LikeSearchOption likePrefix() {
        _like = LIKE_PREFIX;
        return this;
    }
    public LikeSearchOption likeSuffix() {
        _like = LIKE_SUFFIX;
        return this;
    }
    public LikeSearchOption likeContain() {
        _like = LIKE_CONTAIN;
        return this;
    }

    // ===================================================================================
    //                                                                              Escape
    //                                                                              ======
    public LikeSearchOption escapeByPipeLine() {
        _escape = "|";
        return this;
    }

    public LikeSearchOption escapeByAtMark() {
        _escape = "@";
        return this;
    }

    public LikeSearchOption escapeBySlash() {
        _escape = "/";
        return this;
    }

    public LikeSearchOption escapeByBackSlash() {
        _escape = "\\";
        return this;
    }

    // ===================================================================================
    //                                                                               Split
    //                                                                               =====
    public LikeSearchOption splitBySpace() {
        return (LikeSearchOption)doSplitBySpace();
    }

    public LikeSearchOption splitBySpace(int splitLimitCount) {
        return (LikeSearchOption)doSplitBySpace(splitLimitCount);
    }

    public LikeSearchOption splitBySpaceContainsDoubleByte() {
        return (LikeSearchOption)doSplitBySpaceContainsDoubleByte();
    }

    public LikeSearchOption splitBySpaceContainsDoubleByte(int splitLimitCount) {
        return (LikeSearchOption)doSplitBySpaceContainsDoubleByte(splitLimitCount);
    }

    public LikeSearchOption splitByPipeLine() {
        return (LikeSearchOption)doSplitByPipeLine();
    }

    public LikeSearchOption splitByPipeLine(int splitLimitCount) {
        return (LikeSearchOption)doSplitByPipeLine(splitLimitCount);
    }
	
	public LikeSearchOption asOrSplit() {
	    _asOrSplit = true;
		return this;
	}
	
	public boolean isAsOrSplit() {
	    return _asOrSplit;
	}

    // ===================================================================================
    //                                                                 To Upper/Lower Case
    //                                                                 ===================
    public LikeSearchOption toUpperCase() {
        return (LikeSearchOption)doToUpperCase();
    }

    public LikeSearchOption toLowerCase() {
        return (LikeSearchOption)doToLowerCase();
    }

    // ===================================================================================
    //                                                                      To Single Byte
    //                                                                      ==============
    public LikeSearchOption toSingleByteSpace() {
        return (LikeSearchOption)doToSingleByteSpace();
    }

    public LikeSearchOption toSingleByteAlphabetNumber() {
        return (LikeSearchOption)doToSingleByteAlphabetNumber();
    }

    public LikeSearchOption toSingleByteAlphabetNumberMark() {
        return (LikeSearchOption)doToSingleByteAlphabetNumberMark();
    }

    // ===================================================================================
    //                                                                      To Double Byte
    //                                                                      ==============

    // ===================================================================================
    //                                                                            Japanese
    //                                                                            ========
    public JapaneseOptionPartsAgent localJapanese() {
        return doLocalJapanese();
    }

    // ===================================================================================
    //                                                                          Real Value
    //                                                                          ==========
    public String generateRealValue(String value) {
        value = super.generateRealValue(value);

        // Escape
        if (_escape != null && _escape.trim().length() != 0) {
            String tmp = replace(value, _escape, _escape + _escape);
            tmp = replace(tmp, "%", _escape + "%");
            tmp = replace(tmp, "_", _escape + "_");
            if (isCurrentDBDef(DBDef.Oracle)) {
                tmp = replace(tmp, "\uff05", _escape + "\uff05");
                tmp = replace(tmp, "\uff3f", _escape + "\uff3f");
            }
            value = tmp;
        }
        final String wildCard = "%";
        if (_like == null || _like.trim().length() == 0) {
            return value;
        } else if (_like.equals(LIKE_PREFIX)) {
            return value + wildCard;
        } else if (_like.equals(LIKE_SUFFIX)) {
            return wildCard + value;
        } else if (_like.equals(LIKE_CONTAIN)) {
            return wildCard + value + wildCard;
        } else {
            String msg = "The like was wrong string: " + _like;
            throw new IllegalStateException(msg);
        }
    }

    protected boolean isCurrentDBDef(DBDef currentDBDef) {
	    return ResourceContext.isCurrentDBDef(currentDBDef);
    }

    // ===================================================================================
    //                                                                            DeepCopy
    //                                                                            ========
    public Object createDeepCopy() {
        final LikeSearchOption deepCopy = (LikeSearchOption)super.createDeepCopy();
        deepCopy._like = _like;
        deepCopy._escape = _escape;
		if (hasLikeAsOrCallback()) {
            for (Iterator<LikeAsOrCallback> ite = _likeAsOrCallbackList.iterator(); ite.hasNext(); ) {
                deepCopy.addLikeAsOrCallback((LikeAsOrCallback)ite.next());
            }
		}
        return deepCopy;
    }

    protected SimpleStringOption newDeepCopyInstance() {
        return new LikeSearchOption();
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return "like=" + _like + ", escape=" + _escape + ", split=" + isSplit() + ", asOrSplit = " + _asOrSplit;  
    }
}
