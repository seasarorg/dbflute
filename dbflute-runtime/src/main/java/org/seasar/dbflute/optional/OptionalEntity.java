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
public class OptionalEntity<ENTITY> extends OptionalObject<ENTITY> {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final OptionalEntity<Object> EMPTY_INSTANCE;
    static {
        EMPTY_INSTANCE = new OptionalEntity<Object>(null, new OptionalObjectExceptionThrower() {
            public void throwNotFoundException() {
                String msg = "The empty optional so the value is null.";
                throw new EntityAlreadyDeletedException(msg);
            }
        });
    }

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public OptionalEntity(ENTITY entity, OptionalObjectExceptionThrower thrower) { // basically called by DBFlute
        super(entity, thrower);
    }

    @SuppressWarnings("unchecked")
    public static <ENTITY> OptionalEntity<ENTITY> empty() {
        return (OptionalEntity<ENTITY>) EMPTY_INSTANCE;
    }

    // ===================================================================================
    //                                                                     Object Handling
    //                                                                     ===============
    /**
     * Get the entity or exception if null.
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
     * @return The entity instance wrapped in this optional object. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity instance wrapped in this optional object is null, which means entity has already been deleted (point is not found).
     */
    public ENTITY get() {
        return directlyGet();
    }

    /**
     * Handle the entity in the optional object if the entity is present. <br />
     * You should call this if null entity handling is unnecessary (do nothing if null). <br />
     * If exception is preferred when null entity, use required().
     * <pre>
     * opt.<span style="color: #DD4747">ifPresent</span>(member -&gt; {
     *     <span style="color: #3F7E5E">// called if value exists, not called if not present</span>
     *     ... = member.getMemberName();
     * });
     * </pre>
     * @param consumer The callback interface to consume the optional value. (NotNull)
     */
    public void ifPresent(OptionalObjectConsumer<ENTITY> consumer) {
        callbackIfPresent(consumer);
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
        return exists();
    }

    /**
     * Apply the mapping of entity to result object.
     * <pre>
     * OptionalEntity&lt;MemberWebBean&gt; beanOpt = entityOpt.<span style="color: #DD4747">map</span>(member -&gt; {
     *     <span style="color: #3F7E5E">// called if value exists, not called if not present</span>
     *     return new MemberWebBean(member);
     * });
     * </pre>
     * @param mapper The callback interface to apply. (NotNull)
     * @return The optional object as mapped result. (NotNull, EmptyOptionalAllowed: if not present or callback returns null)
     */
    public <RESULT> OptionalEntity<RESULT> map(OptionalObjectFunction<? super ENTITY, ? extends RESULT> mapper) {
        return (OptionalEntity<RESULT>) callbackMapping(mapper); // downcast allowed because factory is overridden
    }

    /**
     * Get the entity instance or null if not present.
     * <pre>
     * MemberCB cb = new MemberCB();
     * cb.query().set...
     * OptionalEntity&lt;Member&gt; entity = memberBhv.selectEntity(cb);
     * Member member = entity.<span style="color: #DD4747">orElseNull</span>() <span style="color: #3F7E5E">// returns null if not present</span>
     * </pre>
     * @return The object instance wrapped in this optional object or null. (NullAllowed: if not present)
     */
    public ENTITY orElseNull() {
        return directlyGetOrElse(null);
    }

    /**
     * Handle the entity in the optional object or exception if not present.
     * <pre>
     * opt.<span style="color: #DD4747">required</span>(member -&gt; {
     *     <span style="color: #3F7E5E">// called if value exists, or exception if not present</span>
     *     ... = member.getMemberName();
     * });
     * </pre>
     * @param consumer The callback interface to consume the optional value. (NotNull)
     * @exception EntityAlreadyDeletedException When the entity instance wrapped in this optional object is null, which means entity has already been deleted (point is not found).
     */
    public void required(OptionalObjectConsumer<ENTITY> consumer) {
        callbackRequired(consumer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <OBJECT> OptionalEntity<OBJECT> createOptionalObject(OBJECT value) {
        return new OptionalEntity<OBJECT>(value, _thrower);
    }
}
