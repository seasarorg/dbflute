package org.seasar.dbflute.util;

import java.util.ArrayList;
import java.util.List;

public class DfTokenUtil {

    /**
     * 指定したデリミタで文字を分割し、
     * Stringの配列で取得することができるメソッド
     * 
     * @param  value     分割対象文字列
     * @param  delimiter デリミタ
     * @return 分割されたString配列
     */
    public static String[] tokenToArgs(String value, String delimiter) {
      List list = tokenToList(value, delimiter);
      return (String[])list.toArray(new String[list.size()]);
    }
    
    /**
     * 指定したデリミタで文字を分割し、
     * Listオブジェクトで取得することができるメソッド
     * 
     * @param  value     分割対象文字列
     * @param  delimiter デリミタ
     * @return 分割されたListオブジェクト
     */
    public static List tokenToList(String value, String delimiter) {
      // 分割した文字を格納する変数
      List list = new ArrayList();
      int i = 0;
      int j = value.indexOf(delimiter);
      for (int h = 0; j >= 0; h++) {
        list.add(value.substring(i, j));
        i = j + 1;
        j = value.indexOf(delimiter, i);
      }
      list.add(value.substring(i));
      return list;
    }
}
