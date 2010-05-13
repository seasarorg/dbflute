/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.exception.thrower;

import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.exception.ColumnQueryInvalidColumnSpecificationException;
import org.seasar.dbflute.exception.DerivedReferrerInvalidForeignSpecificationException;
import org.seasar.dbflute.exception.PagingPageSizeNotPlusException;
import org.seasar.dbflute.exception.QueryDerivedReferrerInvalidColumnSpecificationException;
import org.seasar.dbflute.exception.QueryDerivedReferrerUnmatchedColumnTypeException;
import org.seasar.dbflute.exception.ScalarSelectInvalidColumnSpecificationException;
import org.seasar.dbflute.exception.ScalarSelectInvalidForeignSpecificationException;
import org.seasar.dbflute.exception.ScalarSubQueryInvalidColumnSpecificationException;
import org.seasar.dbflute.exception.ScalarSubQueryInvalidForeignSpecificationException;
import org.seasar.dbflute.exception.ScalarSubQueryUnmatchedColumnTypeException;
import org.seasar.dbflute.exception.SetupSelectAfterUnionException;
import org.seasar.dbflute.exception.SpecifiedDerivedOrderByAliasNameNotFoundException;
import org.seasar.dbflute.exception.SpecifyColumnNotSetupSelectColumnException;
import org.seasar.dbflute.exception.SpecifyDerivedReferrerEntityPropertyNotFoundException;
import org.seasar.dbflute.exception.SpecifyDerivedReferrerInvalidAliasNameException;
import org.seasar.dbflute.exception.SpecifyDerivedReferrerInvalidColumnSpecificationException;
import org.seasar.dbflute.exception.SpecifyDerivedReferrerUnmatchedColumnTypeException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.util.DfSystemUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class ConditionBeanExceptionThrower {

    // ===================================================================================
    //                                                                       Set up Select
    //                                                                       =============
    public void throwSetupSelectAfterUnionException(String className, String foreignPropertyName) {
        String methodName = "setupSelect_" + initCap(foreignPropertyName) + "()";
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("You should NOT call " + methodName + " after calling union()!");
        br.addItem("Advice");
        br.addElement(methodName + " should be called before calling union().");
        br.addElement("For example:");
        br.addElement("  /- - - - - - - - - - - - - - - - - - - - ");
        br.addElement("  " + className + " cb = new " + className + "();");
        br.addElement("  cb." + methodName + "; // You should call here!");
        br.addElement("  cb.query().setXxx...;");
        br.addElement("  cb.union(new UnionQuery<" + className + ">() {");
        br.addElement("      public void query(" + className + " unionCB) {");
        br.addElement("          unionCB.query().setXxx...;");
        br.addElement("      }");
        br.addElement("  });");
        br.addElement("  - - - - - - - - - -/");
        final String msg = br.buildExceptionMessage();
        throw new SetupSelectAfterUnionException(msg);
    }

    // ===================================================================================
    //                                                                              Paging
    //                                                                              ======
    public void throwPagingPageSizeNotPlusException(int pageSize, int pageNumber) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "Page size for paging should not be minus or zero!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Confirm the value of your parameter 'pageSize'." + ln();
        msg = msg + "The first parameter of paging() should be a plus value!" + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    (x) - cb.paging(0, 1);" + ln();
        msg = msg + "    (x) - cb.paging(-3, 2);" + ln();
        msg = msg + "    (o) - cb.paging(4, 3);" + ln();
        msg = msg + ln();
        msg = msg + "[Page Size]" + ln();
        msg = msg + pageSize + ln();
        msg = msg + ln();
        msg = msg + "[Page Number]" + ln();
        msg = msg + pageNumber + ln();
        msg = msg + "* * * * * * * * * */";
        throw new PagingPageSizeNotPlusException(msg);
    }

    // ===================================================================================
    //                                                                      Specify Column
    //                                                                      ==============
    public void throwSpecifyColumnNotSetupSelectColumnException(ConditionBean baseCB, String tableDbName,
            String columnName) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "You specified the column that had Not been Set up!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "You should call setupSelect_[ForeignTable]()"
                + " before calling specify[ForeignTable]().column[TargetColumn]()." + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    (x):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.specify().specifyMemberStatus().columnMemberStatusName(); // *No!" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "    (o):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.setupSelect_MemberStatus(); // *Point!" + ln();
        msg = msg + "    cb.specify().specifyMemberStatus().columnMemberStatusName();" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        // don't use displaySql because of illegal CB's state
        msg = msg + "[ConditionBean]" + ln() + baseCB.getClass().getName() + ln();
        msg = msg + ln();
        msg = msg + "[Specified Column]" + ln() + tableDbName + "." + columnName + ln();
        msg = msg + "* * * * * * * * * */";
        throw new SpecifyColumnNotSetupSelectColumnException(msg);
    }

    // ===================================================================================
    //                                                                       Scalar Select
    //                                                                       =============
    public void throwScalarSelectInvalidColumnSpecificationException(ConditionBean cb, Class<?> resultType) {
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("The specified column for scalar select was invalid");
        br.addItem("Advice");
        br.addElement("You should call specify().column[TargetColumn]() only once.");
        br.addElement("For example:");
        br.addElement("");
        br.addElement("  [Wrong]");
        br.addElement("  /- - - - - - - - - - - - - - - - - - - - ");
        br.addElement("  memberBhv.scalarSelect(Date.class).max(new ScalarQuery<MemberCB>() {");
        br.addElement("      public void query(MemberCB cb) {");
        br.addElement("          // *No! It's empty!");
        br.addElement("      }");
        br.addElement("  });");
        br.addElement("  - - - - - - - - - -/");
        br.addElement("");
        br.addElement("  [Wrong]");
        br.addElement("  /- - - - - - - - - - - - - - - - - - - - ");
        br.addElement("  memberBhv.scalarSelect(Date.class).max(new ScalarQuery<MemberCB>() {");
        br.addElement("      public void query(MemberCB cb) {");
        br.addElement("          cb.specify().columnMemberBirthday();");
        br.addElement("          cb.specify().columnRegisterDatetime(); // *No! It's duplicated!");
        br.addElement("      }");
        br.addElement("  });");
        br.addElement("  - - - - - - - - - -/");
        br.addElement("");
        br.addElement("  [Good]");
        br.addElement("  /- - - - - - - - - - - - - - - - - - - - ");
        br.addElement("  memberBhv.scalarSelect(Date.class).max(new ScalarQuery<MemberCB>() {");
        br.addElement("      public void query(MemberCB cb) {");
        br.addElement("          cb.specify().columnMemberBirthday(); // *point");
        br.addElement("      }");
        br.addElement("  });");
        br.addElement("  - - - - - - - - - -/");
        br.addItem("ConditionBean"); // don't use displaySql because of illegal CB's state
        br.addElement(cb.getClass().getName());
        br.addItem("Result Type");
        br.addElement(resultType.getName());
        final String msg = br.buildExceptionMessage();
        throw new ScalarSelectInvalidColumnSpecificationException(msg);
    }

    public void throwScalarSelectInvalidForeignSpecificationException(String foreignPropertyName) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "You specified a foreign table column in spite of scalar select!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "You should specified a local table column at condition-bean for scalar select." + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    (x):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    memberBhv.scalarSelect(Integer.class).max(new ScalarSelect<MemberCB>() {" + ln();
        msg = msg + "        public void query(MemberCB cb) {" + ln();
        msg = msg + "            cb.specify().specifyMemberStatus().columnDisplayOrder(); // *No!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    });" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "    (o):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    memberBhv.scalarSelect(Date.class).max(new ScalarSelect() {" + ln();
        msg = msg + "        public void query(MemberCB cb) {" + ln();
        msg = msg + "            cb.specify().columnMemberBirthday(); // *Point!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    });" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "[Specified Foreign Property]" + ln() + foreignPropertyName + ln();
        msg = msg + "* * * * * * * * * */";
        throw new ScalarSelectInvalidForeignSpecificationException(msg);
    }

    // ===================================================================================
    //                                                            Specify Derived Referrer
    //                                                            ========================
    public void throwSpecifyDerivedReferrerInvalidAliasNameException(ConditionQuery localCQ) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The alias name for specify derived-referrer was INVALID!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "You should set valid alias name. {NotNull, NotEmpty}" + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    (x):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.specify().derivePurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            subCB.specify().columnPurchaseDatetime();" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }, null); // *No! {null, \"\", \"   \"} are NG!" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "    (o):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.specify().derivePurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            subCB.specify().columnPurchaseDatetime();" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }, \"LATEST_PURCHASE_DATETIME\"); // *Point!" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "[Local Table]" + ln() + localCQ.getTableDbName() + ln();
        msg = msg + "* * * * * * * * * */";
        throw new SpecifyDerivedReferrerInvalidAliasNameException(msg);
    }

    public void throwSpecifyDerivedReferrerEntityPropertyNotFoundException(String aliasName, Class<?> entityType) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "A property for derived-referrer was NOT FOUND in the entity!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "You should implement a property(setter and getter) in the entity." + ln();
        msg = msg + "Or you should confirm whether the alias name has typo or not." + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    ConditionBean Invoking:" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.specify().derivePurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            subCB.specify().columnPurchaseDatetime();" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }, \"LATEST_PURCHASE_DATETIME\");" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "    Extended Entity:" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    // At the entity of Purchase..." + ln();
        msg = msg + "    protected Date _latestPurchaseDatetime;" + ln();
        msg = msg + "    public Date getLatestPurchaseDatetime() {" + ln();
        msg = msg + "        return _latestPurchaseDatetime;" + ln();
        msg = msg + "    }" + ln();
        msg = msg + "    public void setLatestPurchaseDatetime(Date latestPurchaseDatetime) {" + ln();
        msg = msg + "        _latestPurchaseDatetime = latestPurchaseDatetime;" + ln();
        msg = msg + "    }" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "[Alias Name]" + ln() + aliasName + ln();
        msg = msg + ln();
        msg = msg + "[Target Entity]" + ln() + entityType + ln();
        msg = msg + "* * * * * * * * * */";
        throw new SpecifyDerivedReferrerEntityPropertyNotFoundException(msg);
    }

    public void throwSpecifyDerivedReferrerInvalidColumnSpecificationException(String function, String aliasName) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The specified the column for derived-referrer was INVALID!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + " You should call specify().column[TargetColumn]() only once." + ln();
        msg = msg + " (If your function is count(), the target column should be primary key.)" + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    (x):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.specify().derivePurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            // *No! It's empty!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }, \"LATEST_PURCHASE_DATETIME\");" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "    (x):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.specify().derivePurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            subCB.specify().columnPurchaseDatetime();" + ln();
        msg = msg + "            subCB.specify().columnPurchaseCount(); // *No! It's duplicated!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }, \"LATEST_PURCHASE_DATETIME\");" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "    (o):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.specify().derivePurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            subCB.specify().columnPurchaseDatetime(); // *Point!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }, \"LATEST_PURCHASE_DATETIME\");" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "[Function Method]" + ln() + xconvertFunctionToMethod(function) + ln();
        msg = msg + "[Alias Name]" + ln() + aliasName + ln();
        msg = msg + "* * * * * * * * * */";
        throw new SpecifyDerivedReferrerInvalidColumnSpecificationException(msg);
    }

    public void throwSpecifyDerivedReferrerUnmatchedColumnTypeException(String function, String deriveColumnName,
            Class<?> deriveColumnType) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The type of the specified the column unmatched with the function!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "You should confirm the list as follow:" + ln();
        msg = msg + "    count() : String, Number, Date *with distinct same" + ln();
        msg = msg + "    max()   : String, Number, Date" + ln();
        msg = msg + "    min()   : String, Number, Date" + ln();
        msg = msg + "    sum()   : Number" + ln();
        msg = msg + "    avg()   : Number" + ln();
        msg = msg + ln();
        msg = msg + "[Function]" + ln() + function + ln();
        msg = msg + ln();
        msg = msg + "[Derive Column]" + ln() + deriveColumnName + "(" + deriveColumnType.getName() + ")" + ln();
        msg = msg + "* * * * * * * * * */";
        throw new SpecifyDerivedReferrerUnmatchedColumnTypeException(msg);
    }

    public void throwDerivedReferrerInvalidForeignSpecificationException(String foreignPropertyName) { // Query one uses too 
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "You specified a foreign table column in spite of derived-referrer!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "You should specified a local table column at condition-bean for derived-referrer." + ln();
        msg = msg + "  For example(for SpecifyDerivedReferrer):" + ln();
        msg = msg + "    (x):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.specify().derivedPurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            subCB.specify().specifyProduct().columnProductName(); // *No!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }, \"LATEST_PURCHASE_DATETIME\");" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "    (o):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.specify().derivedPurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            subCB.specify().columnPurchaseDatetime();// *Point!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }, \"LATEST_PURCHASE_DATETIME\");" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "[Specified Foreign Property]" + ln() + foreignPropertyName + ln();
        msg = msg + "* * * * * * * * * */";
        throw new DerivedReferrerInvalidForeignSpecificationException(msg);
    }

    // ===================================================================================
    //                                                           Specified Derived OrderBy
    //                                                           =========================
    public void throwSpecifiedDerivedOrderByAliasNameNotFoundException(String aliasName) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The aliasName was not found in specified alias names." + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "You should specified an alias name that is the same as one in specify-derived-referrer." + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    (x):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.specify().derivePurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            subCB.specify().specifyProduct().columnProductName(); // *No!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }, \"LATEST_PURCHASE_DATETIME\");" + ln();
        msg = msg + "    cb.query().addSpecifiedDerivedOrderBy_Desc(\"WRONG_NAME_DATETIME\");" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "    (o):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.specify().derivePurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            subCB.specify().columnPurchaseDatetime();// *Point!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }, \"LATEST_PURCHASE_DATETIME\");" + ln();
        msg = msg + "    cb.query().addSpecifiedDerivedOrderBy_Desc(\"LATEST_PURCHASE_DATETIME\");" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "[not found Alias Name]" + ln() + aliasName + ln();
        msg = msg + "* * * * * * * * * */";
        throw new SpecifiedDerivedOrderByAliasNameNotFoundException(msg);
    }

    // -----------------------------------------------------
    //                                Query Derived Referrer
    //                                ----------------------
    public void throwQueryDerivedReferrerInvalidColumnSpecificationException(String function) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The specified the column for derived-referrer was INVALID!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + " You should call specify().column[TargetColumn]() only once." + ln();
        msg = msg + " (If your function is count(), the target column should be primary key.)" + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    (x):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.query().scalarPurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            // *No! It's empty!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }).greaterEqual(123);" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "    (x):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.query().scalarPurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            subCB.specify().columnPurchaseDatetime();" + ln();
        msg = msg + "            subCB.specify().columnPurchaseCount(); // *No! It's duplicated!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }).greaterEqual(123);" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "    (o):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.query().scalarPurchaseList().max(new SubQuery<PurchaseCB>() {" + ln();
        msg = msg + "        public void query(PurchaseCB subCB) {" + ln();
        msg = msg + "            subCB.specify().columnPurchaseDatetime(); // *Point!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }).greaterEqual(123);" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "[Function Method]" + ln() + xconvertFunctionToMethod(function) + ln();
        msg = msg + "* * * * * * * * * */";
        throw new QueryDerivedReferrerInvalidColumnSpecificationException(msg);
    }

    public void throwQueryDerivedReferrerUnmatchedColumnTypeException(String function, String deriveColumnName,
            Class<?> deriveColumnType, Object value) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The type of the specified the column unmatched with the function or the parameter!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "You should confirm the list as follow:" + ln();
        msg = msg + "    count() : String, Number, Date *with distinct same" + ln();
        msg = msg + "    max()   : String, Number, Date" + ln();
        msg = msg + "    min()   : String, Number, Date" + ln();
        msg = msg + "    sum()   : Number" + ln();
        msg = msg + "    avg()   : Number" + ln();
        msg = msg + ln();
        msg = msg + "[Function Method]" + ln() + xconvertFunctionToMethod(function) + ln();
        msg = msg + ln();
        msg = msg + "[Derive Column]" + ln() + deriveColumnName + "(" + deriveColumnType.getName() + ")" + ln();
        msg = msg + ln();
        msg = msg + "[Parameter Type]" + ln() + (value != null ? value.getClass() : null) + ln();
        msg = msg + "* * * * * * * * * */";
        throw new QueryDerivedReferrerUnmatchedColumnTypeException(msg);
    }

    // ===================================================================================
    //                                                                    Scalar Condition
    //                                                                    ================
    public void throwScalarSubQueryInvalidForeignSpecificationException(String foreignPropertyName) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "You specified a foreign table column in spite of derived-query!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "You should specified a local table column at condition-bean for derived-query." + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    (x):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.query().scalar_Equal().max(new SubQuery<MemberCB>() {" + ln();
        msg = msg + "        public void query(MemberCB subCB) {" + ln();
        msg = msg + "            subCB.specify().specifyMemberStatusName().columnDisplayOrder(); // *No!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    });" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "    (o):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.query().scalar_Equal().max(new SubQuery<MemberCB>() {" + ln();
        msg = msg + "        public void query(MemberCB subCB) {" + ln();
        msg = msg + "            subCB.specify().columnMemberBirthday();// *Point!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    });" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "[Specified Foreign Property]" + ln() + foreignPropertyName + ln();
        msg = msg + "* * * * * * * * * */";
        throw new ScalarSubQueryInvalidForeignSpecificationException(msg);
    }

    public void throwScalarSubQueryInvalidColumnSpecificationException(String function) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The specified the column for derived-referrer was INVALID!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + " You should call specify().column[TargetColumn]() only once." + ln();
        msg = msg + " (If your function is count(), the target column should be primary key.)" + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    (x):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.query().scalar_Equal().max(new SubQuery<MemberCB>() {" + ln();
        msg = msg + "        public void query(MemberCB subCB) {" + ln();
        msg = msg + "            // *No! It's empty!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    });" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "    (x):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.query().scalar_Equal().max(new SubQuery<MemberCB>() {" + ln();
        msg = msg + "        public void query(MemberCB subCB) {" + ln();
        msg = msg + "            subCB.specify().columnMemberBirthday();" + ln();
        msg = msg + "            subCB.specify().columnMemberName(); // *No! It's duplicated!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    });" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "    (o):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.query().scalar_Equal().max(new SubQuery<MemberCB>() {" + ln();
        msg = msg + "        public void query(MemberCB subCB) {" + ln();
        msg = msg + "            subCB.specify().columnPurchaseDatetime(); // *Point!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    });" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "[Function Method]" + ln() + xconvertFunctionToMethod(function) + ln();
        msg = msg + "* * * * * * * * * */";
        throw new ScalarSubQueryInvalidColumnSpecificationException(msg);
    }

    public void throwScalarSubQueryUnmatchedColumnTypeException(String function, String deriveColumnName,
            Class<?> deriveColumnType) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The type of the specified the column unmatched with the function!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "You should confirm the list as follow:" + ln();
        msg = msg + "    max()   : String, Number, Date" + ln();
        msg = msg + "    min()   : String, Number, Date" + ln();
        msg = msg + "    sum()   : Number" + ln();
        msg = msg + "    avg()   : Number" + ln();
        msg = msg + ln();
        msg = msg + "[Function Method]" + ln() + xconvertFunctionToMethod(function) + ln();
        msg = msg + ln();
        msg = msg + "[Derive Column]" + ln() + deriveColumnName + "(" + deriveColumnType.getName() + ")" + ln();
        msg = msg + "* * * * * * * * * */";
        throw new ScalarSubQueryUnmatchedColumnTypeException(msg);
    }

    // ===================================================================================
    //                                                                        Column Query
    //                                                                        ============
    public void throwColumnQueryInvalidColumnSpecificationException() {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The specified the column for column query was INVALID!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + " You should call specify().column[TargetColumn]() only once." + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    (x):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.columnQuery(new SpecifyQuery<MemberCB>() {" + ln();
        msg = msg + "        public void specify(MemberCB cb) {" + ln();
        msg = msg + "            // *No! It's empty!" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }).lessThan...;" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "    (x):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.columnQuery(new SpecifyQuery<MemberCB>() {" + ln();
        msg = msg + "        public void specify(MemberCB cb) {" + ln();
        msg = msg + "            cb.specify().columnMemberName();" + ln();
        msg = msg + "            cb.specify().columnBirthdate();" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }).lessThan...;" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "    (o):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    MemberCB cb = new MemberCB();" + ln();
        msg = msg + "    cb.columnQuery(new SpecifyQuery<MemberCB>() {" + ln();
        msg = msg + "        public void specify(MemberCB cb) {" + ln();
        msg = msg + "            cb.specify().columnBirthdate();" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }).lessThan(new SpecifyQuery<MemberCB>() {" + ln();
        msg = msg + "        public void specify(MemberCB cb) {" + ln();
        msg = msg + "            cb.specify().columnFormalizedDatetime();" + ln();
        msg = msg + "        }" + ln();
        msg = msg + "    }" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + "* * * * * * * * * */";
        throw new ColumnQueryInvalidColumnSpecificationException(msg);
    }

    // -----------------------------------------------------
    //                                       Function Helper
    //                                       ---------------
    protected String xconvertFunctionToMethod(String function) {
        if (function != null && function.contains("(")) { // For example 'count(distinct'
            int index = function.indexOf("(");
            String front = function.substring(0, index);
            if (function.length() > front.length() + "(".length()) {
                String rear = function.substring(index + "(".length());
                function = front + initCap(rear);
            } else {
                function = front;
            }
        }
        return function + "()";
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    /**
     * Get the value of line separator.
     * @return The value of line separator. (NotNull)
     */
    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }

    protected String initCap(String str) {
        return Srl.initCap(str);
    }

    protected String initUncap(String str) {
        return Srl.initUncap(str);
    }

    // ===================================================================================
    //                                                                    Exception Helper
    //                                                                    ================
    protected ExceptionMessageBuilder createExceptionMessageBuilder() {
        return new ExceptionMessageBuilder();
    }
}
