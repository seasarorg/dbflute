package org.seasar.dbflute.logic.sql2entity.analyzer;

/**
 * 
 * @author jflute
 * @since 0.9.8.4 (2011/05/27 Friday)
 */
public class DfSql2EntityMark {

    protected String _content;
    protected String _comment;

    public String getContent() {
        return _content;
    }

    public void setContent(String content) {
        this._content = content;
    }

    public String getComment() {
        return _comment;
    }

    public void setComment(String comment) {
        this._comment = comment;
    }
}
