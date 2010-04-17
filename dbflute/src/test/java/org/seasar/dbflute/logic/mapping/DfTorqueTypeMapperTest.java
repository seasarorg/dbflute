package org.seasar.dbflute.logic.mapping;

import static org.junit.Assert.assertEquals;

import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.torque.engine.database.model.TypeMap;
import org.junit.Test;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.logic.mapping.DfJdbcTypeMapper.Resource;

/**
 * @author jflute
 * @since 0.9.5 (2009/04/21 Tuesday)
 */
public class DfTorqueTypeMapperTest {

    @Test
    public void test_getColumnTorqueType_NameToTorqueTypeMap() {
        initializeEmptyProperty();
        Map<String, String> nameToTorqueTypeMap = new LinkedHashMap<String, String>();
        nameToTorqueTypeMap.put("foo", "bar");
        DfJdbcTypeMapper mapper = new DfJdbcTypeMapper(nameToTorqueTypeMap, new TestResource().java().oracle());
        // ## Act & Assert ##
        assertEquals("bar", mapper.getColumnJdbcType(Types.TIMESTAMP, "foo"));
        assertEquals(TypeMap.TIMESTAMP, mapper.getColumnJdbcType(Types.TIMESTAMP, "bar"));
    }

    @Test
    public void test_getColumnTorqueType_Java_Oracle() {
        initializeEmptyProperty();
        Map<String, String> nameToTorqueTypeMap = new LinkedHashMap<String, String>();
        DfJdbcTypeMapper mapper = new DfJdbcTypeMapper(nameToTorqueTypeMap, new TestResource().java().oracle());

        // ## Act & Assert ##
        assertEquals(TypeMap.TIMESTAMP, mapper.getColumnJdbcType(Types.TIMESTAMP, "timestamp"));
        assertEquals(TypeMap.DATE, mapper.getColumnJdbcType(Types.TIMESTAMP, "date"));
        assertEquals(TypeMap.DATE, mapper.getColumnJdbcType(Types.DATE, "date"));
        assertEquals(TypeMap.VARCHAR, mapper.getColumnJdbcType(Types.VARCHAR, "varchar"));
        assertEquals(TypeMap.VARCHAR, mapper.getColumnJdbcType(Types.OTHER, "nvarchar"));
    }

    @Test
    public void test_getColumnTorqueType_Java_PostgreSQL() {
        initializeEmptyProperty();
        Map<String, String> nameToTorqueTypeMap = new LinkedHashMap<String, String>();
        DfJdbcTypeMapper mapper = new DfJdbcTypeMapper(nameToTorqueTypeMap, new TestResource().java().postgreSQL());

        // ## Act & Assert ##
        assertEquals(TypeMap.TIMESTAMP, mapper.getColumnJdbcType(Types.TIMESTAMP, "timestamp"));
        assertEquals(TypeMap.TIMESTAMP, mapper.getColumnJdbcType(Types.TIMESTAMP, "date"));
        assertEquals(TypeMap.DATE, mapper.getColumnJdbcType(Types.DATE, "date"));
        assertEquals(TypeMap.VARCHAR, mapper.getColumnJdbcType(Types.VARCHAR, "varchar"));
        assertEquals(TypeMap.VARCHAR, mapper.getColumnJdbcType(Types.OTHER, "nvarchar"));
        assertEquals(TypeMap.BLOB, mapper.getColumnJdbcType(Types.OTHER, "oid"));
        assertEquals(TypeMap.UUID, mapper.getColumnJdbcType(Types.OTHER, "uuid"));
    }

    @Test
    public void test_getColumnTorqueType_OriginalMapping() {
        // ## Arrange ##
        Properties prop = new Properties();
        prop.setProperty("torque.typeMappingMap", "map:{FOO=java.bar.Tender}");
        initializeTestProperty(prop);
        Map<String, String> nameToTorqueTypeMap = new LinkedHashMap<String, String>();
        nameToTorqueTypeMap.put("__int4", "FOO");
        DfJdbcTypeMapper mapper = new DfJdbcTypeMapper(nameToTorqueTypeMap, new TestResource().java().oracle());

        // ## Act & Assert ##
        assertEquals("FOO", mapper.getColumnJdbcType(Types.TIMESTAMP, "__int4"));
        assertEquals("java.bar.Tender", TypeMap.findJavaNativeByJdbcType("FOO", 0, 0));
    }

    protected void initializeEmptyProperty() {
        DfBuildProperties.getInstance().setProperties(new Properties());
        DfBuildProperties.getInstance().getHandler().reload();
        TypeMap.reload();
    }

    protected void initializeTestProperty(Properties prop) {
        DfBuildProperties.getInstance().setProperties(prop);
        DfBuildProperties.getInstance().getHandler().reload();
        TypeMap.reload();
    }

    protected static class TestResource implements Resource {
        protected boolean _targetLanguageJava;
        protected boolean _databaseOracle;
        protected boolean _databasePostgreSQL;

        public TestResource java() {
            _targetLanguageJava = true;
            return this;
        }

        public TestResource oracle() {
            _databaseOracle = true;
            return this;
        }

        public TestResource postgreSQL() {
            _databasePostgreSQL = true;
            return this;
        }

        public boolean isLangJava() {
            return _targetLanguageJava;
        }

        public boolean isDbmsOracle() {
            return _databaseOracle;
        }

        public boolean isDbmsPostgreSQL() {
            return _databasePostgreSQL;
        }
    }
}
