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
import org.seasar.dbflute.cbean.chelper.HpCBPurpose;
import org.seasar.dbflute.cbean.ckey.ConditionKey;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.exception.ColumnQueryInvalidColumnSpecificationException;
import org.seasar.dbflute.exception.InvalidQueryRegisteredException;
import org.seasar.dbflute.exception.OrderByIllegalPurposeException;
import org.seasar.dbflute.exception.PagingPageSizeNotPlusException;
import org.seasar.dbflute.exception.QueryDerivedReferrerInvalidColumnSpecificationException;
import org.seasar.dbflute.exception.QueryDerivedReferrerUnmatchedColumnTypeException;
import org.seasar.dbflute.exception.QueryIllegalPurposeException;
import org.seasar.dbflute.exception.RequiredOptionNotFoundException;
import org.seasar.dbflute.exception.ScalarSelectInvalidColumnSpecificationException;
import org.seasar.dbflute.exception.ScalarSelectInvalidForeignSpecificationException;
import org.seasar.dbflute.exception.ScalarSubQueryInvalidColumnSpecificationException;
import org.seasar.dbflute.exception.ScalarSubQueryInvalidForeignSpecificationException;
import org.seasar.dbflute.exception.ScalarSubQueryUnmatchedColumnTypeException;
import org.seasar.dbflute.exception.SetupSelectAfterUnionException;
import org.seasar.dbflute.exception.SetupSelectIllegalPurposeException;
import org.seasar.dbflute.exception.SpecifiedDerivedOrderByAliasNameNotFoundException;
import org.seasar.dbflute.exception.SpecifyColumnNotSetupSelectColumnException;
import org.seasar.dbflute.exception.SpecifyColumnTwoOrMoreColumnException;
import org.seasar.dbflute.exception.SpecifyDerivedReferrerEntityPropertyNotFoundException;
import org.seasar.dbflute.exception.SpecifyDerivedReferrerIllegalPurposeException;
import org.seasar.dbflute.exception.SpecifyDerivedReferrerInvalidAliasNameException;
import org.seasar.dbflute.exception.SpecifyDerivedReferrerInvalidColumnSpecificationException;
import org.seasar.dbflute.exception.SpecifyDerivedReferrerUnmatchedColumnTypeException;
import org.seasar.dbflute.exception.SpecifyIllegalPurposeException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.util.DfSystemUtil;
import org.seasar.dbflute.util.DfTypeUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class ConditionBeanExceptionThrower {

    // ===================================================================================
    //                                                                       Set up Select
    //                                                                       =============
    public void throwSetupSelectIllegalPurposeException(HpCBPurpose purpose, ConditionBean baseCB,
            String foreignPropertyName) {
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("The purpose was illegal for setting up select.");
        br.addItem("Advice");
        br.addElement("This condition-bean is not allowed to set up select.");
        br.addElement("Because this is for " + purpose + ".");
        br.addElement("For example:");
        br.addElement("  (x): (ExistsReferrer)");
        br.addElement("    cb.query().existsXxxList(new SubQuery<PurchaseCB>() {");
        br.addElement("        public void query(PurchaseCB subCB) {");
        br.addElement("            subCB.setupSelect_Product(); // *no!");
        br.addElement("        }");
        br.addElement("    });");
        br.addElement("  (x): (Union)");
        br.addElement("    cb.union(new UnionQuery<MemberCB>() {");
        br.addElement("        public void query(MemberCB unionCB) {");
        br.addElement("            unionCB.setupSelect_MemberStatus(); // *no!");
        br.addElement("        }");
        br.addElement("    });");
        br.addElement("  (o): (Normal Use)");
        br.addElement("    MemberCB cb = new MemberCB();");
        br.addElement("    cb.setupSelect_MemberStatus(); // OK");
        br.addItem("ConditionBean");
        br.addElement(baseCB.getClass().getName());
        br.addItem("Setup Relation");
        br.addElement(foreignPropertyName);
        final String msg = br.buildExceptionMessage();
        throw new SetupSelectIllegalPurposeException(msg);
    }

    public void throwSetupSelectAfterUnionException(ConditionBean baseCB, String foreignPropertyName) {
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("The setup-select was called after union.");
        br.addItem("Advice");
        br.addElement("The setup-select should be called before calling union().");
        br.addElement("For example:");
        br.addElement("  (x):");
        br.addElement("    MemberCB cb = new MemberCB();");
        br.addElement("    cb.query().setXxx...;");
        br.addElement("    cb.union(new UnionQuery<MemberCB>() {");
        br.addElement("        public void query(MemberCB unionCB) {");
        br.addElement("            unionCB.query().setXxx...;");
        br.addElement("        }");
        br.addElement("    });");
        br.addElement("    cb.setupSelect_MemberStatus(); // *no!");
        br.addElement("  (o):");
        br.addElement("    MemberCB cb = new MemberCB();");
        br.addElement("    cb.setupSelect_MemberStatus(); // you should call here");
        br.addElement("    cb.query().setXxx...;");
        br.addElement("    cb.union(new UnionQuery<MemberCB>() {");
        br.addElement("        public void query(MemberCB unionCB) {");
        br.addElement("            unionCB.query().setXxx...;");
        br.addElement("        }");
        br.addElement("    });");
        br.addItem("ConditionBean");
        br.addElement(baseCB.getClass().getName());
        br.addItem("Setup Relation");
        br.addElement(foreignPropertyName);
        final String msg = br.buildExceptionMessage();
        throw new SetupSelectAfterUnionException(msg);
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    public void throwQueryIllegalPurposeException(HpCBPurpose purpose, ConditionBean baseCB) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The purpose was illegal for query.");
        br.addItem("Advice");
        br.addElement("This condition-bean is not allowed to set query.");
        br.addElement("Because this is for " + purpose + ".");
        br.addElement("For example:");
        br.addElement("  (x): (ColumnQuery)");
        br.addElement("    cb.columnQuery(new SpecifyQuery<MemberCB>() {");
        br.addElement("        public void specify(MemberCB cb) {");
        br.addElement("            cb.query().set...(); // *no!");
        br.addElement("        }");
        br.addElement("    })...");
        br.addElement("  (x): (VaryingUpdate)");
        br.addElement("    UpdateOption option = new UpdateOption().self(new SpecifyQuery<MemberCB>() {");
        br.addElement("        public void specify(MemberCB cb) {");
        br.addElement("            cb.query().set...(); // *no!");
        br.addElement("        }");
        br.addElement("    });");
        // don't use displaySql because of illegal CB's state
        br.addItem("ConditionBean");
        br.addElement(baseCB.getClass().getName());
        final String msg = br.buildExceptionMessage();
        throw new QueryIllegalPurposeException(msg);
    }

    public void throwInvalidQueryRegisteredException(ConditionKey key, Object value, String realColumnName) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("An invalid query was registered. (check is working)");
        br.addItem("Advice");
        br.addElement("You should not set an invalid query when the check is valid.");
        br.addElement("For example:");
        br.addElement("  (x):");
        br.addElement("    MemberCB cb = new MemberCB();");
        br.addElement("    cb.checkInvalidQuery();");
        br.addElement("    cb.query().setMemberId_Equal(null); // exception");
        br.addElement("  (o):");
        br.addElement("    MemberCB cb = new MemberCB();");
        br.addElement("    cb.checkInvalidQuery();");
        br.addElement("    cb.query().setMemberId_Equal(3);");
        br.addElement("  (o):");
        br.addElement("    MemberCB cb = new MemberCB();");
        br.addElement("    cb.query().setMemberId_Equal(null);");
        br.addItem("Column");
        br.addElement(realColumnName);
        br.addItem("Condition Key");
        br.addElement(key.getConditionKey());
        br.addItem("Registered Value");
        br.addElement(value);
        final String msg = br.buildExceptionMessage();
        throw new InvalidQueryRegisteredException(msg);
    }

    public void throwLikeSearchOptionNotFoundException(String colName, String value, DBMeta dbmeta) {
        final String capPropName = initCap(dbmeta.findPropertyName(colName));
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The likeSearchOption was not found! (Should not be null!)" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your method call:" + ln();
        final String beanName = DfTypeUtil.toClassTitle(this);
        final String methodName = "set" + capPropName + "_LikeSearch('" + value + "', likeSearchOption);";
        msg = msg + "    " + beanName + "." + methodName + ln();
        msg = msg + "* * * * * * * * * */" + ln();
        throw new RequiredOptionNotFoundException(msg);
    }

    public void throwOrderByIllegalPurposeException(HpCBPurpose purpose, String tableDbName, String columnName) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The purpose was illegal for order-by.");
        br.addItem("Advice");
        br.addElement("This condition-bean is not allowed to order.");
        br.addElement("Because this is for " + purpose + ".");
        br.addElement("For example:");
        br.addElement("  (x): (ExistsReferrer)");
        br.addElement("    cb.query().existsXxxList(new SubQuery<PurchaseCB>() {");
        br.addElement("        public void query(PurchaseCB subCB) {");
        br.addElement("            subCB.query().addOrderBy...; // *no!");
        br.addElement("        }");
        br.addElement("    });");
        br.addElement("  (x): (Union)");
        br.addElement("    cb.union(new UnionQuery<MemberCB>() {");
        br.addElement("        public void query(MemberCB unionCB) {");
        br.addElement("            unionCB.query().addOrderBy...; // *no!");
        br.addElement("        }");
        br.addElement("    });");
        br.addElement("  (x): (DerivedReferrer)");
        br.addElement("    cb.specify().derivedPurchaseList().max(new SubQuery<PurchaseCB>() {");
        br.addElement("        public void query(PurchaseCB subCB) {");
        br.addElement("            subCB.query().addOrderBy...; // *no!");
        br.addElement("        }");
        br.addElement("    });");
        // don't use displaySql because of illegal CB's state
        br.addItem("Order-By Column");
        br.addElement(tableDbName + "." + columnName);
        final String msg = br.buildExceptionMessage();
        throw new OrderByIllegalPurposeException(msg);
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
    //                                                                             Specify
    //                                                                             =======
    public void throwSpecifyIllegalPurposeException(HpCBPurpose purpose, ConditionBean baseCB) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The purpose was illegal for column specification.");
        br.addItem("Advice");
        br.addElement("This condition-bean is not allowed to specify.");
        br.addElement("Because this is for " + purpose + ".");
        br.addElement("For example:");
        br.addElement("  (x): (ExistsReferrer)");
        br.addElement("    cb.query().existsPurchaseList(new SubQuery<PurchaseCB>() {");
        br.addElement("        public void query(PurchaseCB subCB) {");
        br.addElement("            subCB.specify()... // *no!");
        br.addElement("        }");
        br.addElement("    });");
        br.addElement("  (x): (Union)");
        br.addElement("    cb.union(new UnionQuery<PurchaseCB>() {");
        br.addElement("        public void query(PurchaseCB unionCB) {");
        br.addElement("            unionCB.specify()... // *no!");
        br.addElement("        }");
        br.addElement("    });");
        // don't use displaySql because of illegal CB's state
        br.addItem("ConditionBean");
        br.addElement(baseCB.getClass().getName());
        final String msg = br.buildExceptionMessage();
        throw new SpecifyIllegalPurposeException(msg);
    }

    public void throwSpecifyColumnTwoOrMoreColumnException(HpCBPurpose purpose, ConditionBean baseCB, String columnName) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("You specified two or more columns!");
        br.addItem("Advice");
        br.addElement("This condition-bean is not allowed to specify two or more columns.");
        br.addElement("Because the conditoin-bean is for " + purpose + ".");
        br.addElement("For example:");
        br.addElement("  (x): (DerivedReferrer)");
        br.addElement("    cb.specify().derivedPurchaseList().max(new SubQuery<PurchaseCB>() {");
        br.addElement("        public void query(PurchaseCB subCB) {");
        br.addElement("            subCB.specify().columnPurchaseCount();");
        br.addElement("            subCB.specify().columnPurchasePrice(); // *no!");
        br.addElement("        }");
        br.addElement("    });");
        br.addElement("  (x): (ColumnQuery)");
        br.addElement("    cb.columnQuery(new SpecifyQuery<MemberCB>() {");
        br.addElement("        public void specify(MemberCB cb) {");
        br.addElement("            cb.specify().columnMemberName();");
        br.addElement("            cb.specify().columnBirthdate(); // *no!");
        br.addElement("        }");
        br.addElement("    })...");
        br.addElement("  (o): (DerivedReferrer)");
        br.addElement("    cb.specify().derivedPurchaseList().max(new SubQuery<PurchaseCB>() {");
        br.addElement("        public void query(PurchaseCB subCB) {");
        br.addElement("            subCB.specify().columnPurchaseCount();");
        br.addElement("        }");
        br.addElement("    });");
        // don't use displaySql because of illegal CB's state
        br.addItem("ConditionBean");
        br.addElement(baseCB.getClass().getName());
        br.addItem("Specified Column");
        br.addElement(baseCB.getTableDbName() + "." + columnName);
        final String msg = br.buildExceptionMessage();
        throw new SpecifyColumnTwoOrMoreColumnException(msg);
    }

    public void throwSpecifyColumnNotSetupSelectColumnException(ConditionBean baseCB, String columnName) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("You specified the column that had not been set up!");
        br.addItem("Advice");
        br.addElement("You should call setupSelect_[ForeignTable]()");
        br.addElement("before calling specify[ForeignTable]().column[TargetColumn]().");
        br.addElement("For example:");
        br.addElement("  (x):");
        br.addElement("    MemberCB cb = new MemberCB();");
        br.addElement("    cb.specify().specifyMemberStatus().columnMemberStatusName(); // *no!");
        br.addElement("  (o):");
        br.addElement("    MemberCB cb = new MemberCB();");
        br.addElement("    cb.setupSelect_MemberStatus(); // *point!");
        br.addElement("    cb.specify().specifyMemberStatus().columnMemberStatusName();");
        // don't use displaySql because of illegal CB's state
        br.addItem("ConditionBean");
        br.addElement(baseCB.getClass().getName());
        br.addItem("Specified Column");
        br.addElement(baseCB.getTableDbName() + "." + columnName);
        final String msg = br.buildExceptionMessage();
        throw new SpecifyColumnNotSetupSelectColumnException(msg);
    }

    public void throwSpecifyDerivedReferrerIllegalPurposeException(HpCBPurpose purpose, ConditionBean baseCB,
            String referrerName) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The purpose was illegal for derived-referrer specification.");
        br.addItem("Advice");
        br.addElement("This condition-bean is not allowed to specify a derived referrer.");
        br.addElement("Because this is for " + purpose + ".");
        br.addElement("For example:");
        br.addElement("  (x): (DerivedReferrer)");
        br.addElement("    cb.specify().derivedPurchaseList().max(new SubQuery<PurchaseCB>() {");
        br.addElement("        public void query(PurchaseCB subCB) {");
        br.addElement("            subCB.specify().derivedPurchaseList()...; // *no!");
        br.addElement("        }");
        br.addElement("    });");
        // don't use displaySql because of illegal CB's state
        br.addItem("ConditionBean");
        br.addElement(baseCB.getClass().getName());
        br.addItem("Specified Referrer");
        br.addElement(referrerName);
        final String msg = br.buildExceptionMessage();
        throw new SpecifyDerivedReferrerIllegalPurposeException(msg);
    }

    // ===================================================================================
    //                                                                       Scalar Select
    //                                                                       =============
    public void throwScalarSelectInvalidColumnSpecificationException(ConditionBean cb, Class<?> resultType) {
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("The specified column for scalar select was invalid.");
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

    public void throwScalarSelectInvalidForeignSpecificationException(String relationName) {
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
        msg = msg + "[Specified Relation]" + ln() + relationName + ln();
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
    public void throwScalarSubQueryInvalidForeignSpecificationException(String relationName) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "You specified a foreign table column in spite of scalar-condition!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "You should specified a local table column at condition-bean for scalar-condition." + ln();
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
        msg = msg + "[Specified Relation]" + ln() + relationName + ln();
        msg = msg + "* * * * * * * * * */";
        throw new ScalarSubQueryInvalidForeignSpecificationException(msg);
    }

    public void throwScalarSubQueryInvalidColumnSpecificationException(String function) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The specified the column for scalar-condition was INVALID!" + ln();
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
