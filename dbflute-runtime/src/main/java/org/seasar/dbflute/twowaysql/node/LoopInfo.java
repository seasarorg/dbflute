package org.seasar.dbflute.twowaysql.node;

import java.util.List;

import org.seasar.dbflute.cbean.coption.LikeSearchOption;

/**
 * @author jflute
 * @since 0.9.7.0 (2010/05/29 Saturday)
 */
public class LoopInfo {

    protected List<?> _parameterList;
    protected int _loopSize;
    protected LikeSearchOption _likeSearchOption;
    protected int _loopIndex;

    public List<?> getParameterList() {
        return _parameterList;
    }

    public void setParameterList(List<?> parameterList) {
        this._parameterList = parameterList;
    }

    public int getLoopSize() {
        return _loopSize;
    }

    public void setLoopSize(int loopSize) {
        this._loopSize = loopSize;
    }

    public LikeSearchOption getLikeSearchOption() {
        return _likeSearchOption;
    }

    public void setLikeSearchOption(LikeSearchOption likeSearchOption) {
        this._likeSearchOption = likeSearchOption;
    }

    public int getLoopIndex() {
        return _loopIndex;
    }

    public void setLoopIndex(int loopIndex) {
        this._loopIndex = loopIndex;
    }

    public Object getCurrentParameter() {
        return _parameterList.get(_loopIndex);
    }
}