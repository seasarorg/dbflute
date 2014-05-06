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
package org.seasar.dbflute;

import org.seasar.dbflute.exception.EntityAlreadyDeletedException;
import org.seasar.dbflute.exception.thrower.OptionalValueExceptionThrower;
import org.seasar.dbflute.util.DfTypeUtil;

/**
 * The entity as optional object, which has entity instance in it. <br />
 * You can handle null value by this methods without direct null handling.
 * <pre>
 * MemberCB cb = new MemberCB();
 * cb.query().set...
 * OptionalEntity&lt;Member&gt; entity = memberBhv.selectEntity(cb);
 *
 * <span style="color: #3F7E5E">// if the data always exists as your business rule</span>
 * Member member = entity.get();
 *
 * <span style="color: #3F7E5E">// if it might be no data, isPresent(), orElse(), ...</span>
 * if (entity.isPresent()) {
 *     Member member = entity.get();
 * } else {
 *     ...
 * }
 * </pre>
 * @param <ENTITY> The type of entity.
 * @author jflute
 * @since 1.0.5F (2014/05/05 Monday)
 */
public class OptionalEntity<ENTITY> {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final OptionalEntity<Object> EMPTY_INSTANCE;
    static {
        EMPTY_INSTANCE = new OptionalEntity<Object>(null, new OptionalValueExceptionThrower() {
            public void throwNotFoundException() {
                String msg = "The empty optional so the value is null.";
                throw new EntityAlreadyDeletedException(msg);
            }
        });
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The entity instance for this optional object. (NullAllowed) */
    protected final ENTITY _entity;

    /** The exception thrower e.g. when entity value is not-found. (NotNull) */
    protected final OptionalValueExceptionThrower _thrower;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public OptionalEntity(ENTITY entity, OptionalValueExceptionThrower thrower) { // basically called by DBFlute
        _entity = entity; // may be null
        if (thrower == null) {
            String msg = "The argument should not be null: entity=" + entity;
            throw new IllegalArgumentException(msg);
        }
        _thrower = thrower;
    }

    @SuppressWarnings("unchecked")
    public static <ENTITY> OptionalEntity<ENTITY> empty() {
        return (OptionalEntity<ENTITY>) EMPTY_INSTANCE;
    }

    // ===================================================================================
    //                                                                      Value Handling
    //                                                                      ==============
    /**
     * Get the entity or throw exception if null.
     * <pre>
     * MemberCB cb = new MemberCB();
     * cb.query().set...
     * OptionalEntity&lt;Member&gt; entity = memberBhv.selectEntity(cb);
     *
     * <span style="color: #3F7E5E">// if the data always exists as your business rule</span>
     * Member member = entity.get();
     *
     * <span style="color: #3F7E5E">// if it might be no data, isPresent(), orElse(), ...</span>
     * if (entity.isPresent()) {
     *     Member member = entity.get();
     * } else {
     *     ...
     * }
     * </pre>
     * @return The entity instance saved in this optional object. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity instance saved in this optional object is null, which means entity has already been deleted (point is not found).
     */
    public ENTITY get() {
        if (!isPresent()) {
            _thrower.throwNotFoundException();
        }
        return _entity;
    }

    /**
     * Is the entity instance present? (existing?)
     * <pre>
     * MemberCB cb = new MemberCB();
     * cb.query().set...
     * OptionalEntity&lt;Member&gt; entity = memberBhv.selectEntity(cb);
     * if (entity.isPresent()) { <span style="color: #3F7E5E">// true if the entity exists</span>
     *     Member member = entity.get();
     * } else {
     *     ...
     * }
     * </pre>
     * @return The determination, true or false.
     */
    public boolean isPresent() {
        return _entity != null;
    }

    /**
     * Get the entity instance or specified entity if null.
     * <pre>
     * MemberCB cb = new MemberCB();
     * cb.query().set...
     * OptionalEntity&lt;Member&gt; entity = memberBhv.selectEntity(cb);
     * Member other = ...
     * Member member = entity.orElse(other) <span style="color: #3F7E5E">// returns other instance if null</span>
     * </pre>
     * @param other The other instance to be returned if null. (NullAllowed: if null, returns null if entity is null)
     * @return The determination, true or false.
     */
    public ENTITY orElse(ENTITY other) {
        return isPresent() ? _entity : other;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public int hashCode() {
        return _entity != null ? _entity.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OptionalEntity<?>) {
            final OptionalEntity<?> other = (OptionalEntity<?>) obj;
            if (_entity != null) {
                return _entity.equals(other.get());
            } else { // null v.s. null?
                return !other.isPresent();
            }
        }
        return false;
    }

    @Override
    public String toString() {
        final String title = DfTypeUtil.toClassTitle(this);
        return title + ":{" + (_entity != null ? _entity.toString() : "null") + "}";
    }
}
