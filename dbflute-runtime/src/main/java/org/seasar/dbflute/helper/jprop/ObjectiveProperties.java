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
package org.seasar.dbflute.helper.jprop;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.DfTypeUtil;
import org.seasar.dbflute.util.DfTypeUtil.ParseDateException;
import org.seasar.dbflute.util.DfTypeUtil.ParseDateNumberFormatException;
import org.seasar.dbflute.util.DfTypeUtil.ParseDateOutOfCalendarException;

/**
 * @author jflute
 * @since 1.0.1 (2012/12/30 Sunday)
 */
public class ObjectiveProperties {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The resource path of (base-point) properties loaded by class loader. (NotNull) */
    protected final String _resourcePath;

    // -----------------------------------------------------
    //                                                Option
    //                                                ------
    /** The list of resource path of extends properties loaded by class loader. (NullAllowed) */
    protected List<String> _extendsResourcePathList = DfCollectionUtil.newArrayListSized(4);

    /** Does it check the implicit override property? */
    protected boolean _checkImplicitOverride;

    // -----------------------------------------------------
    //                                              Contents
    //                                              --------
    /** The result of java properties reading. (NotNull: after loading) */
    protected JavaPropertiesResult _javaPropertiesResult;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * @param resourcePath The path of the property as resource for this class loader. (NotNull)
     */
    public ObjectiveProperties(String resourcePath) {
        _resourcePath = resourcePath;
    }

    // -----------------------------------------------------
    //                                                Option
    //                                                ------
    public ObjectiveProperties extendsProperties(String... extendsResourcePaths) {
        if (extendsResourcePaths != null && extendsResourcePaths.length > 0) {
            for (String extendsResourcePath : extendsResourcePaths) {
                _extendsResourcePathList.add(extendsResourcePath);
            }
        }
        return this;
    }

    public ObjectiveProperties checkImplicitOverride() {
        _checkImplicitOverride = true;
        return this;
    }

    // ===================================================================================
    //                                                                     Load Properties
    //                                                                     ===============
    /**
     * Load properties. <br />
     * You can get properties after loading.
     * @return this. (NotNull)
     */
    public ObjectiveProperties load() {
        final String title = toTitle(_resourcePath);
        final JavaPropertiesReader reader = new JavaPropertiesReader(title, new JavaPropertiesStreamProvider() {
            public InputStream provideStream() throws IOException {
                return toStream(_resourcePath);
            }
        });
        prepareExtendsProperties(reader);
        if (_checkImplicitOverride) {
            reader.checkImplicitOverride();
        }
        _javaPropertiesResult = reader.read();
        return this;
    }

    protected void prepareExtendsProperties(final JavaPropertiesReader reader) {
        for (final String extendsResourcePath : _extendsResourcePathList) {
            final String title = toTitle(extendsResourcePath);
            reader.extendsProperties(title, new JavaPropertiesStreamProvider() {
                public InputStream provideStream() throws IOException {
                    return toStream(extendsResourcePath);
                }
            });
        }
    }

    protected String toTitle(String path) {
        return DfTypeUtil.toClassTitle(this) + ":" + path;
    }

    protected InputStream toStream(String resourcePath) {
        return getClass().getClassLoader().getResourceAsStream(resourcePath);
    }

    // ===================================================================================
    //                                                                        Get Property
    //                                                                        ============
    /**
     * Get the value of property as {@link String}.
     * @param propertyKey The key of the property. (NotNull)
     * @return The value of found property. (NullAllowed: if null, not found)
     */
    public String get(String propertyKey) {
        final JavaPropertiesProperty property = _javaPropertiesResult.getProperty(propertyKey);
        return property != null ? property.getPropertyValue() : null;
    }

    /**
     * Get the value of property as {@link Integer}.
     * @param propertyKey The key of the property. (NotNull)
     * @return The value of found property. (NullAllowed: if null, not found)
     * @throws NumberFormatException When the property is not integer.
     */
    public Integer getAsInteger(String propertyKey) {
        final String value = get(propertyKey);
        return value != null ? DfTypeUtil.toInteger(value) : null;
    }

    /**
     * Get the value of property as {@link Long}.
     * @param propertyKey The key of the property. (NotNull)
     * @return The value of found property. (NullAllowed: if null, not found)
     * @throws NumberFormatException When the property is not long.
     */
    public Long getAsLong(String propertyKey) {
        final String value = get(propertyKey);
        return value != null ? DfTypeUtil.toLong(value) : null;
    }

    /**
     * Get the value of property as {@link BigDecimal}.
     * @param propertyKey The key of the property. (NotNull)
     * @return The value of found property. (NullAllowed: if null, not found)
     * @throws NumberFormatException When the property is not decimal.
     */
    public BigDecimal getAsDecimal(String propertyKey) {
        final String value = get(propertyKey);
        return value != null ? DfTypeUtil.toBigDecimal(value) : null;
    }

    /**
     * Get the value of property as {@link Date}.
     * @param propertyKey The key of the property. (NotNull)
     * @return The value of found property. (NullAllowed: if null, not found)
     * @throws ParseDateException When it failed to parse the string to date.
     * @throws ParseDateNumberFormatException When it failed to format the elements as number.
     * @throws ParseDateOutOfCalendarException When the date was out of calendar. (if BC, not thrown)
     */
    public Date getAsDate(String propertyKey) {
        final String value = get(propertyKey);
        return value != null ? DfTypeUtil.toDate(value) : null;
    }

    /**
     * Is the property true?
     * @param propertyKey The key of the property which is boolean type. (NotNull)
     * @return The determination, true or false. (if the property can be true, returns true)
     */
    public boolean is(String propertyKey) {
        final String value = get(propertyKey);
        return value != null && value.trim().equalsIgnoreCase("true");
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ObjectiveProperties)) {
            return false;
        }
        final ObjectiveProperties another = (ObjectiveProperties) obj;
        if (_javaPropertiesResult == null) {
            return another._javaPropertiesResult == null;
        }
        return _javaPropertiesResult.equals(another._javaPropertiesResult);
    }

    @Override
    public int hashCode() {
        return _javaPropertiesResult != null ? _javaPropertiesResult.hashCode() : 0;
    }

    @Override
    public String toString() {
        return DfTypeUtil.toClassTitle(this) + ":{" + _javaPropertiesResult + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getResourcePath() {
        return _resourcePath;
    }

    public List<String> getExtendsResourcePathList() {
        return _extendsResourcePathList;
    }

    public boolean isCheckImplicitOverride() {
        return _checkImplicitOverride;
    }

    public JavaPropertiesResult getJavaPropertiesResult() {
        return _javaPropertiesResult;
    }
}
