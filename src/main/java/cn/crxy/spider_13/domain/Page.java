package cn.crxy.spider_13.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Page {
	
	/**
	 * 页面原始内容
	 */
	private String content;
	
	/**
	 * 原始url
	 */
	private String url;
	
	/**
	 * 存储商品的基本参数信息
	 */
	private Map<String,String> values = new HashMap<String, String>();
	
	/**
	 * 商品原始编号
	 */
	private String goodsid;
	
	/**
	 * 存储列表页面临时解析的url
	 */
	private List<String> urls = new ArrayList<String>();
	

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, String> getValues() {
		return values;
	}
	
	public void addField(String key,String value){
		this.values.put(key, value);
	}

	public String getGoodsid() {
		return goodsid;
	}

	public void setGoodsid(String goodsid) {
		this.goodsid = goodsid;
	}

	public List<String> getUrls() {
		return urls;
	}
	
	public void addUrl(String url){
		this.urls.add(url);
	}
	
}
