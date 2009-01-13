package com.example.dbflute.basic.dbflute.exentity;

import java.util.Date;

/**
 * The entity of MEMBER.
 * @author DBFlute(AutoGenerator)
 */
public class Member extends com.example.dbflute.basic.dbflute.bsentity.BsMember {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** 導出カラム「最終ログイン日時」のためのプロパティ。これは手動で作成する。 */
    protected Date _latestLoginDatetime;

    /** 導出カラム「ログイン回数」のためのプロパティ。これは手動で作成する。 */
    protected Integer _loginCount;

    /** 導出カラム「プロダクト種類数」のためのプロパティ。これは手動で作成する。 */
    protected Integer _productKindCount;

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Date getLatestLoginDatetime() {
        return _latestLoginDatetime;
    }

    public void setLatestLoginDatetime(Date latestLoginDatetime) {
        _latestLoginDatetime = latestLoginDatetime;
    }

    public Integer getLoginCount() {
        return _loginCount;
    }

    public void setLoginCount(Integer loginCount) {
        this._loginCount = loginCount;
    }

    public Integer getProductKindCount() {
        return _productKindCount;
    }

    public void setProductKindCount(Integer productKindCount) {
        this._productKindCount = productKindCount;
    }
}
