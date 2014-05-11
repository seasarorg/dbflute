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

import org.seasar.dbflute.exception.EntityAlreadyDeletedException;

// #later optional property, making now
/**
 * @param <VALUE> The type of value.
 * @author jflute
 * @since 1.0.5F (2014/05/11 Sunday)
 */
public class OptionalProperty<VALUE> extends OptionalObject<VALUE> {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final OptionalProperty<Object> EMPTY_INSTANCE;
    static {
        EMPTY_INSTANCE = new OptionalProperty<Object>(null, new OptionalObjectExceptionThrower() {
            public void throwNotFoundException() {
                String msg = "The empty optional so the value is null.";
                throw new EntityAlreadyDeletedException(msg);
            }
        });
    }

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public OptionalProperty(VALUE value) { // might be called by user
        super(value, new OptionalObjectExceptionThrower() {
            public void throwNotFoundException() {
                String msg = "Not found the value in the optional property: (unknown)";
                throw new IllegalStateException(msg);
            }
        });
    }

    public OptionalProperty(VALUE value, final String tableDbName, final String columnName) { // basically called by DBFlute
        super(value, new OptionalObjectExceptionThrower() {
            public void throwNotFoundException() {
                String msg = "Not found the value in the optional property: " + tableDbName + "." + columnName;
                throw new IllegalStateException(msg);
            }
        });
    }

    public OptionalProperty(VALUE value, OptionalObjectExceptionThrower thrower) { // basically called by DBFlute
        super(value, thrower);
    }

    @SuppressWarnings("unchecked")
    public static <EMPTY> OptionalProperty<EMPTY> empty() {
        return (OptionalProperty<EMPTY>) EMPTY_INSTANCE;
    }

    // ===================================================================================
    //                                                                     Object Handling
    //                                                                     ===============
    public VALUE get() {
        return directlyGet();
    }

    public void ifPresent(OptionalObjectConsumer<VALUE> consumer) {
        callbackIfPresent(consumer);
    }

    public boolean isPresent() {
        return exists();
    }

    public <RESULT> OptionalProperty<RESULT> map(OptionalObjectFunction<? super VALUE, ? extends RESULT> mapper) {
        return (OptionalProperty<RESULT>) callbackMapping(mapper); // downcast allowed because factory is overridden
    }

    public VALUE orElse(VALUE other) {
        return directlyGetOrElse(other);
    }

    public VALUE orElseNull() {
        return directlyGetOrElse(null);
    }

    public void required(OptionalObjectConsumer<VALUE> consumer) {
        callbackRequired(consumer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <OBJECT> OptionalProperty<OBJECT> createOptionalObject(OBJECT value) {
        return new OptionalProperty<OBJECT>(value, _thrower);
    }
}
