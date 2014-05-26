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
import org.seasar.dbflute.exception.NonSetupSelectRelationAccessException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;

/**
 * The entity as optional object, which has entity instance in it. <br />
 * You can handle null value by this methods without direct null handling.
 * <pre>
 * MemberCB cb = new MemberCB();
 * cb.query().set...
 * OptionalEntity&lt;Member&gt; entity = memberBhv.selectEntity(cb);
 *
 * <span style="color: #3F7E5E">// if the data always exists as your business rule</span>
 * entity.<span style="color: #DD4747">required</span>(member -&gt; {
 *     ...
 * });
 * Member member = entity.<span style="color: #DD4747">get()</span>;
 *
 * <span style="color: #3F7E5E">// if it might be no data, isPresent(), orElseNull(), ...</span>
 * entity.<span style="color: #DD4747">ifPresent</span>(member -&gt; {
 *     ...
 * });
 * if (entity.<span style="color: #DD4747">isPresent()</span>) {
 *     Member member = entity.<span style="color: #DD4747">get()</span>;
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
    protected static final OptionalObjectExceptionThrower NOWAY_THROWER = new OptionalObjectExceptionThrower() {
        public void throwNotFoundException() {
            throw new EntityAlreadyDeletedException("no way");
        }
    };
    protected static final OptionalEntity<Object> RELATION_EMPTY_INSTANCE;
    static {
        RELATION_EMPTY_INSTANCE = new OptionalEntity<Object>(null, new OptionalObjectExceptionThrower() {
            public void throwNotFoundException() {
                // TODO jflute exception message for relation
                final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
                br.addNotice("NON-setupSelect relation was accessed.");
                br.addItem("Advice");
                br.addElement("Confirm your access to the relation.");
                br.addElement("Call setupSelect or fix your access.");
                br.addElement("For example:");
                br.addElement("  (x):");
                br.addElement("    MemberCB cb = new MemberCB()");
                br.addElement("    cb.setupSelect_MemberStatus();");
                br.addElement("    List<Member> memberList = memberBhv.selectList(cb);");
                br.addElement("    for (Member member : memberList) {");
                br.addElement("        ... = member.getMemberSecurityAsOne().required(...); // *NG");
                br.addElement("    }");
                br.addElement("  (o):");
                br.addElement("    MemberCB cb = new MemberCB()");
                br.addElement("    cb.setupSelect_MemberStatus();");
                br.addElement("    List<Member> memberList = memberBhv.selectList(cb);");
                br.addElement("    for (Member member : memberList) {");
                br.addElement("        ... = member.getMemberStatus().required(...); // OK");
                br.addElement("    }");
                br.addElement("  (o):");
                br.addElement("    MemberCB cb = new MemberCB()");
                br.addElement("    cb.setupSelect_MemberSecurityAsOne(); // OK");
                br.addElement("    List<Member> memberList = memberBhv.selectList(cb);");
                br.addElement("    for (Member member : memberList) {");
                br.addElement("        ... = member.getMemberSecurityAsOne().required(...);");
                br.addElement("    }");
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
    public static <EMPTY> OptionalEntity<EMPTY> empty() {
        return (OptionalEntity<EMPTY>) EMPTY_INSTANCE;
    }

    public static <ENTITY> OptionalEntity<ENTITY> of(ENTITY entity) {
        return new OptionalEntity<ENTITY>(entity, NOWAY_THROWER);
    }

    public static <ENTITY> OptionalEntity<ENTITY> ofNullable(ENTITY entity, OptionalObjectExceptionThrower thrower) {
        if (entity != null) {
            return of(entity);
        } else {
            return new OptionalEntity<ENTITY>(entity, thrower);
        }
    }

    public static <EMPTY> OptionalEntity<EMPTY> relationEmpty(final Object entity, final String relation) {
        return new OptionalEntity<EMPTY>(null, new OptionalObjectExceptionThrower() {
            public void throwNotFoundException() {
                throwNonSetupSelectRelationAccessException(entity, relation);
            }
        });
    }

    protected static void throwNonSetupSelectRelationAccessException(Object entity, String relation) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("NON-setupSelect relation was accessed.");
        br.addItem("Advice");
        br.addElement("Confirm your access to the relation.");
        br.addElement("Call setupSelect or fix your access.");
        br.addElement("For example:");
        br.addElement("  (x):");
        br.addElement("    MemberCB cb = new MemberCB()");
        br.addElement("    cb.setupSelect_MemberStatus();");
        br.addElement("    List<Member> memberList = memberBhv.selectList(cb);");
        br.addElement("    for (Member member : memberList) {");
        br.addElement("        ... = member.getMemberSecurityAsOne().required(...); // *NG");
        br.addElement("    }");
        br.addElement("  (o): (fix access mistake)");
        br.addElement("    MemberCB cb = new MemberCB()");
        br.addElement("    cb.setupSelect_MemberStatus();");
        br.addElement("    List<Member> memberList = memberBhv.selectList(cb);");
        br.addElement("    for (Member member : memberList) {");
        br.addElement("        ... = member.getMemberStatus().required(...); // OK");
        br.addElement("    }");
        br.addElement("  (o): (fix setupSelect mistake)");
        br.addElement("    MemberCB cb = new MemberCB()");
        br.addElement("    cb.setupSelect_MemberSecurityAsOne(); // OK");
        br.addElement("    List<Member> memberList = memberBhv.selectList(cb);");
        br.addElement("    for (Member member : memberList) {");
        br.addElement("        ... = member.getMemberSecurityAsOne().required(...);");
        br.addElement("    }");
        if (entity != null) { // just in case
            br.addItem("Your Relation");
            br.addElement(entity.getClass().getSimpleName() + "." + relation);
        }
        final String msg = br.buildExceptionMessage();
        throw new NonSetupSelectRelationAccessException(msg);
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
     * Member member = entity.<span style="color: #DD4747">get()</span>;
     *
     * <span style="color: #3F7E5E">// if it might be no data, isPresent(), orElse(), ...</span>
     * if (entity.<span style="color: #DD4747">isPresent()</span>) {
     *     Member member = entity.<span style="color: #DD4747">get()</span>;
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
     * MemberCB cb = new MemberCB();
     * cb.query().set...
     * OptionalEntity&lt;Member&gt; entity = memberBhv.selectEntity(cb);
     * entity.<span style="color: #DD4747">ifPresent</span>(member -&gt; {
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
     * if (entity.<span style="color: #DD4747">isPresent()</span>) { <span style="color: #3F7E5E">// true if the entity exists</span>
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
     * MemberCB cb = new MemberCB();
     * cb.query().set...
     * OptionalEntity&lt;Member&gt; entity = memberBhv.selectEntity(cb);
     * OptionalEntity&lt;MemberWebBean&gt; bean = entity.<span style="color: #DD4747">map</span>(member -&gt; {
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

    // absolutely no needed
    //public ENTITY orElse(ENTITY other) {
    //    return directlyGetOrElse(other);
    //}

    /**
     * Get the entity instance or null if not present.
     * <pre>
     * MemberCB cb = new MemberCB();
     * cb.query().set...
     * OptionalEntity&lt;Member&gt; entity = memberBhv.selectEntity(cb);
     * Member member = entity.<span style="color: #DD4747">orElseNull()</span> <span style="color: #3F7E5E">// returns null if not present</span>
     * </pre>
     * @return The object instance wrapped in this optional object or null. (NullAllowed: if not present)
     */
    public ENTITY orElseNull() {
        return directlyGetOrElse(null);
    }

    /**
     * Handle the entity in the optional object or exception if not present.
     * <pre>
     * MemberCB cb = new MemberCB();
     * cb.query().set...
     * OptionalEntity&lt;Member&gt; entity = memberBhv.selectEntity(cb);
     * entity.<span style="color: #DD4747">required</span>(member -&gt; {
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
