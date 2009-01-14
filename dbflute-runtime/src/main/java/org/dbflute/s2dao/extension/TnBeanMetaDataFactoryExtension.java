package org.dbflute.s2dao.extension;

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.dbflute.Entity;
import org.dbflute.s2dao.identity.TnIdentifierGenerator;
import org.dbflute.s2dao.identity.TnIdentifierGeneratorFactory;
import org.dbflute.s2dao.metadata.TnBeanMetaData;
import org.dbflute.s2dao.metadata.impl.TnBeanMetaDataFactoryImpl;
import org.dbflute.s2dao.metadata.impl.TnBeanMetaDataImpl;
import org.dbflute.s2dao.metadata.TnPropertyType;
import org.dbflute.s2dao.beans.TnPropertyDesc;

/**
 * BeanMetaDataFactoryImpl for DBFlute.
 * @author DBFlute(AutoGenerator)
 */
@SuppressWarnings("unchecked")
public class TnBeanMetaDataFactoryExtension extends TnBeanMetaDataFactoryImpl {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The map of bean meta data for cache. */
    protected Map<Class<? extends Entity>, TnBeanMetaData> _metaMap = newConcurrentHashMap();

    // ===================================================================================
    //                                                                  Override for Cache
    //                                                                  ==================
    @Override
    public TnBeanMetaData createBeanMetaData(Class beanClass) {
        final TnBeanMetaData cachedMeta = findCachedMeta(beanClass);
        if (cachedMeta != null) {
            return cachedMeta;
        } else {
            return super.createBeanMetaData(beanClass);
        }
    }

    @Override
    public TnBeanMetaData createBeanMetaData(Class beanClass, int relationNestLevel) {
        final TnBeanMetaData cachedMeta = findCachedMeta(beanClass);
        if (cachedMeta != null) {
            return cachedMeta;
        } else {
            return super.createBeanMetaData(beanClass, relationNestLevel);
        }
    }

    @Override
    public TnBeanMetaData createBeanMetaData(DatabaseMetaData dbMetaData, Class beanClass, int relationNestLevel) {
        final TnBeanMetaData cachedMeta = findOrCreateCachedMetaIfNeeds(dbMetaData, beanClass, relationNestLevel);
        if (cachedMeta != null) {
            return cachedMeta;
        } else {
            return super.createBeanMetaData(dbMetaData, beanClass, relationNestLevel);
        }
    }

    protected TnBeanMetaData findCachedMeta(Class beanClass) {
        if (isDBFluteEntity(beanClass)) {
            final TnBeanMetaData cachedMeta = getMetaFromCache(beanClass);
            if (cachedMeta != null) {
                return cachedMeta;
            }
        }
        return null;
    }

    protected TnBeanMetaData findOrCreateCachedMetaIfNeeds(DatabaseMetaData dbMetaData, Class beanClass,
            int relationNestLevel) {
        if (isDBFluteEntity(beanClass)) {
            final TnBeanMetaData cachedMeta = getMetaFromCache(beanClass);
            if (cachedMeta != null) {
                return cachedMeta;
            } else {
                return super.createBeanMetaData(dbMetaData, beanClass, 0);
            }
        }
        return null;
    }

    // ===================================================================================
    //                                                               BeanMetaData Creation
    //                                                               =====================
    @Override
    protected TnBeanMetaDataImpl createBeanMetaDataImpl() {
        // /= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
        // for ConditionBean and insert() and update() and delete() and so on...
        // = = = = = = = = = =/
        return new TnBeanMetaDataImpl() {
            protected List<TnIdentifierGenerator> _internalIdentifierGeneratorList = new ArrayList<TnIdentifierGenerator>();
            protected Map<String, TnIdentifierGenerator> _internalIdentifierGeneratorsByPropertyName = newConcurrentHashMap();

            // /= = = = = = =
            // for cache
            // = = = = =/
            @Override
            public void initialize() { // for Cache
                final Class myBeanClass = getBeanClass();
                if (isDBFluteEntity(myBeanClass)) {
                    final TnBeanMetaData cachedMeta = getMetaFromCache(myBeanClass);
                    if (cachedMeta == null) {
                        _metaMap.put(myBeanClass, this);
                    }
                }
                super.initialize();
            }

            // /= = = = = = =
            // for insert()
            // = = = = =/
            // The attributes 'identifierGenerators' and 'identifierGeneratorsByPropertyName'
            // of super class are unused. It prepares original atributes here.
            @Override
            protected void setupIdentifierGenerator(TnPropertyType propertyType) {
                final TnPropertyDesc pd = propertyType.getPropertyDesc();
                final String propertyName = propertyType.getPropertyName();
                final String idType = beanAnnotationReader.getId(pd);
                final TnIdentifierGenerator generator = createInternalIdentifierGenerator(propertyType, idType);
                _internalIdentifierGeneratorList.add(generator);
                _internalIdentifierGeneratorsByPropertyName.put(propertyName, generator);
            }

            protected TnIdentifierGenerator createInternalIdentifierGenerator(TnPropertyType propertyType, String idType) {
                return TnIdentifierGeneratorFactory.createIdentifierGenerator(propertyType, idType);
            }

            @Override
            public TnIdentifierGenerator getIdentifierGenerator(int index) {
                return _internalIdentifierGeneratorList.get(index);
            }

            @Override
            public int getIdentifierGeneratorSize() {
                return _internalIdentifierGeneratorList.size();
            }

            @Override
            public TnIdentifierGenerator getIdentifierGenerator(String propertyName) {
                return _internalIdentifierGeneratorsByPropertyName.get(propertyName);
            }
        };
    }

    // /= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
    // Other extension points about BeanMetaDataImpl is as follows:
    // 
    //   S2DaoMetaDataExtension.createNonConcurrencyBmdFactory()
    //     --> for NonConcurrency (updateNonstrict() and deleteNonstrict() and so on...) 
    // 
    //   S2DaoMetaDataExtension.createOutsideSqlCustomizeBeanMetaDataFactory()
    //     --> for OutsideSql but it overrides nothing.
    // = = = = = = = = = =/

    // ===================================================================================
    //                                                                 Relation Next Level
    //                                                                 ===================
    /**
     * Get the limit nest level of relation.
     * @return The limit nest level of relation.
     */
    @Override
    protected int getLimitRelationNestLevel() {
        return 2; // for Compatible to old version DBFlute
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected <KEY, VALUE> ConcurrentHashMap<KEY, VALUE> newConcurrentHashMap() {
        return new ConcurrentHashMap<KEY, VALUE>();
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected boolean isDBFluteEntity(Class beanClass) {
        return Entity.class.isAssignableFrom(beanClass);
    }

    protected TnBeanMetaData getMetaFromCache(Class beanClass) {
        return _metaMap.get(beanClass);
    }
}