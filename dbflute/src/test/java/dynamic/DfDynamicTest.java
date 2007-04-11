package dynamic;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.seasar.dbflute.util.DfNameHintUtil;
import org.seasar.dbflute.util.DfTokenUtil;

import sun.reflect.Reflection;


public class DfDynamicTest {

    public DfDynamicTest() {
    }

    @Test
    public void test_dynamic() {
        xxx();
//        final Class callerClass = Reflection.getCallerClass(2);
//        System.out.println(callerClass);
//        System.out.println(callerClass.getClassLoader());
    }
    
    protected void xxx() {
        final Class callerClass = Reflection.getCallerClass(2);
        System.out.println(callerClass);
    }
}
