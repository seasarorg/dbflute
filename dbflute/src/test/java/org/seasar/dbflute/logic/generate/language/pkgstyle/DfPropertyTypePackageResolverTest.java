/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.logic.generate.language.pkgstyle;

import org.seasar.dbflute.unit.core.PlainTestCase;

/**
 * @author jflute
 */
public class DfPropertyTypePackageResolverTest extends PlainTestCase {

    public void test_doResolvePackageName_Java_Date_basic() throws Exception {
        // ## Arrange ##
        DfLanguagePropertyPackageResolver resolver = createJavaTarget();

        // ## Act & Assert ##
        assertEquals("java.util.Date", resolver.doResolvePackageName("Date", false));
        assertEquals("Date", resolver.doResolvePackageName("Date", true));
    }

    public void test_doResolvePackageName_Java_List_basic() throws Exception {
        // ## Arrange ##
        DfLanguagePropertyPackageResolver resolver = createJavaTarget();

        // ## Act & Assert ##
        assertEquals("java.util.List<String>", resolver.doResolvePackageName("List<String>", false));
        assertEquals("List<String>", resolver.doResolvePackageName("List<String>", true));
    }

    public void test_doResolvePackageName_Java_List_nest() throws Exception {
        // ## Arrange ##
        DfLanguagePropertyPackageResolver resolver = createJavaTarget();

        // ## Act ##
        String actual = resolver.doResolvePackageName("List<List<Date>>", false);

        // ## Assert ##
        assertEquals("java.util.List<java.util.List<java.util.Date>>", actual);
    }

    public void test_doResolvePackageName_Java_List_nest_Map() throws Exception {
        // ## Arrange ##
        DfLanguagePropertyPackageResolver resolver = createJavaTarget();

        // ## Act ##
        String actual = resolver.doResolvePackageName("List<List<Map<Date, Date>>>", false);

        // ## Assert ##
        log(actual);
        assertEquals("java.util.List<java.util.List<java.util.Map<Date, java.util.Date>>>", actual);
    }

    public void test_doResolvePackageName_Java_Map_basic() throws Exception {
        // ## Arrange ##
        DfLanguagePropertyPackageResolver resolver = createJavaTarget();

        // ## Act & Assert ##
        assertEquals("java.util.Map<String, String>", resolver.doResolvePackageName("Map<String, String>", false));
        assertEquals("java.util.Map<String, java.util.Date>", resolver.doResolvePackageName("Map<String, Date>", false));
    }

    public void test_doResolvePackageName_Java_Map_nest() throws Exception {
        // ## Arrange ##
        DfLanguagePropertyPackageResolver resolver = createJavaTarget();

        // ## Act ##
        String actual = resolver.doResolvePackageName("Map<Date, List<Map<Date, Date>>>", false);

        // ## Assert ##
        log(actual);
        assertEquals("java.util.Map<Date, java.util.List<java.util.Map<Date, java.util.Date>>>", actual);
    }

    public void test_doResolvePackageName_CSharp_List_nest_Map() throws Exception {
        // ## Arrange ##
        DfLanguagePropertyPackageResolver resolver = createCSharpTarget();

        // ## Act ##
        String actual = resolver.doResolvePackageName("IList<IList<IDictionary<Date, Date>>>", false);

        // ## Assert ##
        log(actual);
        assertEquals(
                "System.Collections.Generic.IList<System.Collections.Generic.IList<System.Collections.Generic.IDictionary<Date, Date>>>",
                actual);
    }

    protected DfLanguagePropertyPackageResolver createJavaTarget() {
        return new DfLanguagePropertyPackageResolverJava();
    }

    protected DfLanguagePropertyPackageResolver createCSharpTarget() {
        return new DfLanguagePropertyPackageResolverCSharp();
    }
}
