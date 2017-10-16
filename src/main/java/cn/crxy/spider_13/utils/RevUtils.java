package cn.crxy.spider_13.utils;
public class RevUtils {
	
	/**
	 * 字符串反转
	 * @param s
	 * @return
	 */
	public static String reverse(String s) {
		  char[] array = s.toCharArray();
		  String reverse = "";
		  for (int i = array.length - 1; i >= 0; i--)
		   reverse += array[i];
		  return reverse;
	 }

}
