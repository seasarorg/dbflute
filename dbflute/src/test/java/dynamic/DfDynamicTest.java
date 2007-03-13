package dynamic;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.seasar.dbflute.util.DfNameHintUtil;
import org.seasar.dbflute.util.DfTokenUtil;


public class DfDynamicTest {

    public DfDynamicTest() {
    }

    @Test
    public void test_dynamic() {
        
        final String target = "aaa\tbbb\tccc\t\t\t\t";
        
        final List tokenToList = DfTokenUtil.tokenToList(target, "\t");
        final String[] split = target.split("\t");
        
        System.out.println(split.length);
        System.out.println(tokenToList.size());
    }       
}
