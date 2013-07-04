package org.seasar.dbflute.infra.dfprop;

import java.util.Map;

import org.seasar.dbflute.unit.core.PlainTestCase;

/**
 * @author jflute
 */
public class DfPropFileTest extends PlainTestCase {

    // ===================================================================================
    //                                                                      Switched Style
    //                                                                      ==============
    /**
     * <pre>
     * dbflute_exampledb
     *  |-dfprop
     *  |  |-maihama
     *  |  |  |-exampleMap.dfprop  // env
     *  |  |-exampleMap.dfprop     // main
     *  
     * env: default
     * </pre>
     * @throws Exception 
     */
    public void test_readMap_SwitchStyle_default() throws Exception {
        // ## Arrange ##
        DfPropFile propFile = createSwitchStylePropFile();

        // ## Act ##
        Map<String, Object> map = propFile.readMap("/dfprop/exampleMap.dfprop", null);

        // ## Assert ##
        log(map);
        assertEquals(2, map.size());
        assertEquals(map.get("main"), "mainValue");
        assertEquals(map.get("main*"), "mainValue*");
    }

    /**
     * <pre>
     * dbflute_exampledb
     *  |-dfprop
     *  |  |-maihama
     *  |  |  |-exampleMap.dfprop  // env
     *  |  |-exampleMap.dfprop     // main
     *  
     * env: maihama
     * </pre>
     * @throws Exception 
     */
    public void test_readMap_SwitchStyle_maihama() throws Exception {
        // ## Arrange ##
        DfPropFile propFile = createSwitchStylePropFile();

        // ## Act ##
        Map<String, Object> map = propFile.readMap("/dfprop/exampleMap.dfprop", "maihama");

        // ## Assert ##
        log(map);
        assertEquals(1, map.size());
        map.put("env", "envValue");
    }

    /**
     * <pre>
     * dbflute_exampledb
     *  |-dfprop
     *  |  |-maihama
     *  |  |  |-exampleMap.dfprop  // env
     *  |  |-exampleMap.dfprop     // main
     *  
     * env: noexists
     * </pre>
     * @throws Exception 
     */
    public void test_readMap_SwitchStyle_noexists() throws Exception {
        // ## Arrange ##
        DfPropFile propFile = createSwitchStylePropFile();

        // ## Act ##
        Map<String, Object> map = propFile.readMap("/dfprop/exampleMap.dfprop", "noexists");

        // ## Assert ##
        log(map);
        assertEquals(2, map.size());
        assertEquals(map.get("main"), "mainValue");
        assertEquals(map.get("main*"), "mainValue*");
    }

    protected DfPropFile createSwitchStylePropFile() {
        return new DfPropFile() {
            protected <ELEMENT> Map<String, ELEMENT> callReadingMapChecked(DfPropReadingMapHandler<ELEMENT> handler,
                    String path) {
                return prepareSwitchStyleMap(path);
            }
        };
    }

    @SuppressWarnings("unchecked")
    protected <ELEMENT> Map<String, ELEMENT> prepareSwitchStyleMap(String path) {
        Map<String, Object> mockMap = newLinkedHashMap();
        if ("/dfprop/exampleMap.dfprop".equals(path)) {
            mockMap.put("main", "mainValue");
            mockMap.put("main*", "mainValue*");
        } else if ("/dfprop/maihama/exampleMap.dfprop".equals(path)) {
            mockMap.put("env", "envValue");
        } else {
            mockMap = null;
        }
        return (Map<String, ELEMENT>) mockMap;
    }

    /**
     * <pre>
     * dbflute_exampledb
     *  |-dfprop
     *  |  |-maihama
     *  |  |  |-exampleMap.dfprop  // env
     *  |  |-exampleMap.dfprop     // main
     *  
     * env: noexists
     * </pre>
     * @throws Exception 
     */
    public void test_readMap_SwitchStyle_emptySwitch() throws Exception {
        // ## Arrange ##
        DfPropFile propFile = new DfPropFile() {
            @SuppressWarnings("unchecked")
            @Override
            protected <ELEMENT> Map<String, ELEMENT> callReadingMapChecked(DfPropReadingMapHandler<ELEMENT> handler,
                    String path) {
                Map<String, Object> mockMap = newLinkedHashMap();
                if ("/dfprop/exampleMap.dfprop".equals(path)) {
                    mockMap.put("main", "mainValue");
                    mockMap.put("main*", "mainValue*");
                } else if ("/dfprop/maihama/exampleMap.dfprop".equals(path)) {
                } else {
                    mockMap = null;
                }
                return (Map<String, ELEMENT>) mockMap;
            }
        };

        // ## Act ##
        Map<String, Object> map = propFile.readMap("/dfprop/exampleMap.dfprop", "maihama");

        // ## Assert ##
        log(map);
        assertHasZeroElement(map.keySet());
    }

    // ===================================================================================
    //                                                                       Inherit Style
    //                                                                       =============
    /**
     * <pre>
     * dbflute_exampledb
     *  |-dfprop
     *  |  |-maihama
     *  |  |  |-exampleMap+.dfprop // env+
     *  |  |-exampleMap.dfprop     // main
     *  
     * env: default
     * </pre>
     * @throws Exception 
     */
    public void test_readMap_InheritStyle_default() throws Exception {
        // ## Arrange ##
        DfPropFile propFile = createInheritStylePropFile();

        // ## Act ##
        Map<String, Object> map = propFile.readMap("/dfprop/exampleMap.dfprop", null);

        // ## Assert ##
        log(map);
        assertEquals(2, map.size());
        assertEquals(map.get("main"), "mainValue");
        assertEquals(map.get("main*"), "mainValue*");
    }

    /**
     * <pre>
     * dbflute_exampledb
     *  |-dfprop
     *  |  |-maihama
     *  |  |  |-exampleMap+.dfprop // env
     *  |  |-exampleMap.dfprop     // main
     *  
     * env: maihama
     * </pre>
     * @throws Exception 
     */
    public void test_readMap_InheritStyle_maihama() throws Exception {
        // ## Arrange ##
        DfPropFile propFile = createInheritStylePropFile();

        // ## Act ##
        Map<String, Object> map = propFile.readMap("/dfprop/exampleMap.dfprop", "maihama");

        // ## Assert ##
        log(map);
        assertEquals(3, map.size());
        assertEquals(map.get("main"), "mainValue");
        assertEquals(map.get("main*"), "envValue*");
        assertEquals(map.get("env+"), "envPlusValue");
    }

    /**
     * <pre>
     * dbflute_exampledb
     *  |-dfprop
     *  |  |-maihama
     *  |  |  |-exampleMap+.dfprop // env+
     *  |  |-exampleMap.dfprop     // main
     *  
     * env: noexists
     * </pre>
     * @throws Exception 
     */
    public void test_readMap_InheritStyle_noexists() throws Exception {
        // ## Arrange ##
        DfPropFile propFile = createInheritStylePropFile();

        // ## Act ##
        Map<String, Object> map = propFile.readMap("/dfprop/exampleMap.dfprop", "noexists");

        // ## Assert ##
        log(map);
        assertEquals(2, map.size());
        assertEquals(map.get("main"), "mainValue");
        assertEquals(map.get("main*"), "mainValue*");
    }

    protected DfPropFile createInheritStylePropFile() {
        return new DfPropFile() {
            protected <ELEMENT> Map<String, ELEMENT> callReadingMapChecked(DfPropReadingMapHandler<ELEMENT> handler,
                    String path) {
                return prepareInheritStyleMap(path);
            }
        };
    }

    @SuppressWarnings("unchecked")
    protected <ELEMENT> Map<String, ELEMENT> prepareInheritStyleMap(String path) {
        Map<String, Object> mockMap = newLinkedHashMap();
        if ("/dfprop/exampleMap.dfprop".equals(path)) {
            mockMap.put("main", "mainValue");
            mockMap.put("main*", "mainValue*");
        } else if ("/dfprop/maihama/exampleMap+.dfprop".equals(path)) {
            mockMap.put("env+", "envPlusValue");
            mockMap.put("main*", "envValue*");
        } else {
            mockMap = null;
        }
        return (Map<String, ELEMENT>) mockMap;
    }

    /**
     * <pre>
     * dbflute_exampledb
     *  |-dfprop
     *  |  |-maihama
     *  |  |  |-exampleMap+.dfprop // env
     *  |  |-exampleMap.dfprop     // main
     *  
     * env: maihama
     * </pre>
     * @throws Exception 
     */
    public void test_readMap_InheritStyle_emptyInherit() throws Exception {
        // ## Arrange ##
        DfPropFile propFile = new DfPropFile() {
            @SuppressWarnings("unchecked")
            @Override
            protected <ELEMENT> Map<String, ELEMENT> callReadingMapChecked(DfPropReadingMapHandler<ELEMENT> handler,
                    String path) {
                Map<String, Object> mockMap = newLinkedHashMap();
                if ("/dfprop/exampleMap.dfprop".equals(path)) {
                    mockMap.put("main", "mainValue");
                    mockMap.put("main*", "mainValue*");
                } else if ("/dfprop/maihama/exampleMap+.dfprop".equals(path)) {
                } else {
                    mockMap = null;
                }
                return (Map<String, ELEMENT>) mockMap;
            }
        };

        // ## Act ##
        Map<String, Object> map = propFile.readMap("/dfprop/exampleMap.dfprop", "maihama");

        // ## Assert ##
        log(map);
        assertEquals(2, map.size());
        assertEquals(map.get("main"), "mainValue");
        assertEquals(map.get("main*"), "mainValue*");
    }

    // ===================================================================================
    //                                                                           All Stars
    //                                                                           =========
    /**
     * <pre>
     * dbflute_exampledb
     *  |-dfprop
     *  |  |-maihama
     *  |  |  |-exampleMap.dfprop  // env
     *  |  |  |-exampleMap+.dfprop // env+
     *  |  |-exampleMap.dfprop     // main
     *  |  |-exampleMap+.dfprop    // main+
     *  
     * env: default
     * </pre>
     * @throws Exception 
     */
    public void test_readMap_AllStars_default() throws Exception {
        // ## Arrange ##
        DfPropFile propFile = createAllStarsPropFile();

        // ## Act ##
        Map<String, Object> map = propFile.readMap("/dfprop/exampleMap.dfprop", null);

        // ## Assert ##
        log(map);
        assertEquals(3, map.size());
        assertEquals(map.get("main"), "mainValue");
        assertEquals(map.get("main+"), "mainPlusValue");
        assertEquals(map.get("main*"), "mainValue*");
    }

    /**
     * <pre>
     * dbflute_exampledb
     *  |-dfprop
     *  |  |-maihama
     *  |  |  |-exampleMap.dfprop  // env
     *  |  |  |-exampleMap+.dfprop // env+
     *  |  |-exampleMap.dfprop     // main
     *  |  |-exampleMap+.dfprop    // main+
     *  
     * env: maihama
     * </pre>
     * @throws Exception 
     */
    public void test_readMap_AllStars_maihama() throws Exception {
        // ## Arrange ##
        DfPropFile propFile = createAllStarsPropFile();

        // ## Act ##
        Map<String, Object> map = propFile.readMap("/dfprop/exampleMap.dfprop", "maihama");

        // ## Assert ##
        log(map);
        assertEquals(3, map.size());
        assertEquals(map.get("env"), "envValue");
        assertEquals(map.get("env+"), "envPlusValue");
        assertEquals(map.get("main*"), "envValue*");
    }

    /**
     * <pre>
     * dbflute_exampledb
     *  |-dfprop
     *  |  |-maihama
     *  |  |  |-exampleMap.dfprop  // env
     *  |  |  |-exampleMap+.dfprop // env+
     *  |  |-exampleMap.dfprop     // main
     *  |  |-exampleMap+.dfprop    // main+
     *  
     * env: noexists
     * </pre>
     * @throws Exception 
     */
    public void test_readMap_AllStars_noExists() throws Exception {
        // ## Arrange ##
        DfPropFile propFile = createAllStarsPropFile();

        // ## Act ##
        Map<String, Object> map = propFile.readMap("/dfprop/exampleMap.dfprop", "noexists");

        // ## Assert ##
        log(map);
        assertEquals(3, map.size());
        assertEquals(map.get("main"), "mainValue");
        assertEquals(map.get("main*"), "mainValue*");
        assertEquals(map.get("main+"), "mainPlusValue");
    }

    protected DfPropFile createAllStarsPropFile() {
        return new DfPropFile() {
            protected <ELEMENT> Map<String, ELEMENT> callReadingMapChecked(DfPropReadingMapHandler<ELEMENT> handler,
                    String path) {
                return prepareAllStarsMap(path);
            }
        };
    }

    @SuppressWarnings("unchecked")
    protected <ELEMENT> Map<String, ELEMENT> prepareAllStarsMap(String path) {
        Map<String, Object> mockMap = newLinkedHashMap();
        if ("/dfprop/exampleMap.dfprop".equals(path)) {
            mockMap.put("main", "mainValue");
            mockMap.put("main*", "mainValue*");
        } else if ("/dfprop/exampleMap+.dfprop".equals(path)) {
            mockMap.put("main+", "mainPlusValue");
        } else if ("/dfprop/maihama/exampleMap.dfprop".equals(path)) {
            mockMap.put("env", "envValue");
        } else if ("/dfprop/maihama/exampleMap+.dfprop".equals(path)) {
            mockMap.put("env+", "envPlusValue");
            mockMap.put("main*", "envValue*");
        } else {
            mockMap = null;
        }
        return (Map<String, ELEMENT>) mockMap;
    }
}
