package cn.crxy.spider_13.utils;

import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

public class HtmlUtils {
	
	/**
	 * 根据xpath获取指定标签的内容
	 * @param tagNode
	 * @param xpath
	 * @return
	 */
	public static String getText(TagNode tagNode,String xpath){
		String result = null;
		try {
			Object[] nodeObjs = tagNode.evaluateXPath(xpath);
			if(nodeObjs!=null && nodeObjs.length>0){
				TagNode node = (TagNode)nodeObjs[0];
				result = node.getText().toString();
			}
		} catch (XPatherException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 根据xpath获取指定标签的指定属性的值
	 * @param tagNode
	 * @param xpath
	 * @param attr
	 * @return
	 */
	public static String getAttributeByName(TagNode tagNode,String xpath,String attr){
		String result = null;
		try {
			Object[] nodeObjs = tagNode.evaluateXPath(xpath);
			if(nodeObjs!=null && nodeObjs.length>0){
				TagNode node = (TagNode)nodeObjs[0];
				result = node.getAttributeByName(attr);
			}
		} catch (XPatherException e) {
			e.printStackTrace();
		}
		return result;
	}

}
