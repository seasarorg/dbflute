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
package org.seasar.dbflute.optional;

import org.seasar.dbflute.util.DfTypeUtil;

/**
 * The base class for optional object.
 * @param <VALUE> The type of wrapped value in the optional object.
 * @author jflute
 * @since 1.0.5F (2014/05/10 Saturday)
 */
public abstract class OptionalObject<VALUE> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The value for this optional object. (NullAllowed) */
    protected final VALUE _value;

    /** The exception thrower e.g. when value is not-found. (NotNull) */
    protected final OptionalObjectExceptionThrower _thrower;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public OptionalObject(VALUE value, OptionalObjectExceptionThrower thrower) { // basically called by DBFlute
        _value = value; // may be null
        if (thrower == null) {
            String msg = "The argument 'thrower' should not be null: value=" + value;
            throw new IllegalArgumentException(msg);
        }
        _thrower = thrower;
    }

    // ===================================================================================
    //                                                                     Object Handling
    //                                                                     ===============
    /**
     * @return The value instance wrapped in this optional object. (NotNull)
     */
    protected VALUE directlyGet() {
        if (!exists()) {
            _thrower.throwNotFoundException();
        }
        return _value;
    }

    /**
     * @param consumer The callback interface to consume the wrapped value. (NotNull)
     */
    protected void callbackIfPresent(OptionalObjectConsumer<VALUE> consumer) {
        if (consumer == null) {
            String msg = "The argument 'consumer' should not be null.";
            throw new IllegalArgumentException(msg);
        }
        if (exists()) {
            consumer.accept(_value);
        }
    }

    /**
     * Is the object instance present? (existing?)
     * @return The determination, true or false.
     */
    protected boolean exists() {
        return _value != null;
    }

    /**
     * @param mapper The callback interface to apply. (NotNull)
     * @return The optional object as mapped result. (NotNull, EmptyOptionalAllowed: if not present or callback returns null)
     */
    protected <RESULT> OptionalObject<RESULT> callbackMapping(
            OptionalObjectFunction<? super VALUE, ? extends RESULT> mapper) {
        if (mapper == null) {
            String msg = "The argument 'mapper' should not be null.";
            throw new IllegalArgumentException(msg);
        }
        final RESULT result = exists() ? mapper.apply(_value) : null;
        return createOptionalObject(result);
    }

    /**
     * @param other The other instance to be returned if null. (NullAllowed: if null, returns null when entity is null)
     * @return The value instance wrapped in this optional object or specified value. (NullAllowed: if null specified)
     */
    protected VALUE directlyGetOrElse(VALUE other) {
        return exists() ? _value : other;
    }

    /**
     * @param consumer The callback interface to consume the wrapped value. (NotNull)
     */
    protected void callbackRequired(OptionalObjectConsumer<VALUE> consumer) {
        if (consumer == null) {
            String msg = "The argument 'consumer' should not be null.";
            throw new IllegalArgumentException(msg);
        }
        if (_value == null) {
            _thrower.throwNotFoundException();
        }
        consumer.accept(_value);
    }

    /**
     * @param <ARG> The type of value for optional object.
     * @param value The plain value for the optional object. (NullAllowed: if null, return s empty optional)
     * @return The new-created instance of optional object. (NotNull)
     */
    protected abstract <ARG> OptionalObject<ARG> createOptionalObject(ARG value);

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public int hashCode() {
        return _value != null ? _value.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass().isInstance(obj)) {
            final OptionalObject<?> other = (OptionalObject<?>) obj;
            if (_value != null) {
                return _value.equals(other.directlyGet());
            } else { // null v.s. null?
                return !other.exists();
            }
        }
        return false;
    }

    @Override
    public String toString() {
        final String title = DfTypeUtil.toClassTitle(this);
        return title + ":{" + (_value != null ? _value.toString() : "null") + "}";
    }
}
