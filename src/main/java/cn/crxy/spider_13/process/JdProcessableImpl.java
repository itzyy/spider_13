package cn.crxy.spider_13.process;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.json.JSONArray;
import org.json.JSONObject;

import cn.crxy.spider_13.domain.Page;
import cn.crxy.spider_13.utils.HtmlUtils;
import cn.crxy.spider_13.utils.PageUtils;
import cn.crxy.spider_13.utils.RevUtils;

/**
 * 这种规则建议每个电商网站提起一套，保存在数据库，方便后期修改
 * <root>
 * <title type="xpath">//div[@id="title"]</title>
 * <picpath type="url">http://aa.ss.com/</picpath>
 * </root>
 * @author Administrator
 *
 */
public class JdProcessableImpl implements Processable {

	@Override
	public void process(Page page) {
		HtmlCleaner htmlCleaner = new HtmlCleaner();
		try {
			//表示对页面内容进行封装，转换成一个tagnode对象
			TagNode rootNode = htmlCleaner.clean(page.getContent());
			if(page.getUrl().startsWith("http://list.jd.com/list.html")){
				//下一页
				String nexturl = HtmlUtils.getAttributeByName(rootNode, "//*[@id=\"J_topPage\"]/a[2]", "href");
				if(!"javascript:;".equals(nexturl)){//针对最后一页的下一页url处理
					page.addUrl("http://list.jd.com"+nexturl);
				}
				//当前页面商品的所有url
				Object[] goodurlobjs = rootNode.evaluateXPath("//*[@id=\"plist\"]/ul/li/div/div[1]/a");
				for (Object goodObj : goodurlobjs) {
					TagNode goodNode = (TagNode)goodObj;
					String goodUrl = goodNode.getAttributeByName("href");
					page.addUrl("http:"+goodUrl);
				}
			}else{//商品明细页面
				parsePruduct(page, rootNode);
			}
		} catch (XPatherException e) {
			e.printStackTrace();
		}
	}



	public void parsePruduct(Page page, TagNode rootNode)
			throws XPatherException {
		//标题
		/*Object[] titleObjs = rootNode.evaluateXPath("//div[@class='sku-name']");
		if(titleObjs!=null && titleObjs.length>0){
			TagNode titleNode = (TagNode)titleObjs[0];
			page.addField("title", titleNode.getText().toString());
		}*/
		String title = HtmlUtils.getText(rootNode, "//div[@class='sku-name']");
		page.addField("title", title);
		
		//图片地址 data-origin
		/*Object[] picpathObjs = rootNode.evaluateXPath("//*[@id=\"spec-img\"]");
		if(picpathObjs!=null && picpathObjs.length>0){
			TagNode picNode = (TagNode)picpathObjs[0];
			String picpath = picNode.getAttributeByName("data-origin");
			page.addField("picpath", "http:"+picpath);
		}*/
		String picpath = HtmlUtils.getAttributeByName(rootNode, "//*[@id=\"spec-img\"]", "data-origin");
		page.addField("picpath", "http:"+picpath);
		
		//价格 由于价格是异步加载的，所以需要分析js请求，模拟访问获取价格
		/*Object[] priceObjs = rootNode.evaluateXPath("//span[@class='p-price']/span[2]");
		if(priceObjs!=null && priceObjs.length>0){
			TagNode priceNode = (TagNode)priceObjs[0];
			System.out.println("---"+priceNode.getText().toString()+"---");
		}*/
		String price = getPrice(page);
		page.addField("price", price);
		//规格参数
		JSONArray specArray = getSpec(rootNode);
		page.addField("spec", specArray.toString());
	}
	
	
	
	public JSONArray getSpec(TagNode rootNode) throws XPatherException {
		JSONArray specArray = new JSONArray();
		Object[] itemObjs = rootNode.evaluateXPath("//*[@id=\"detail\"]/div[2]/div[2]/div[2]/div");
		for (Object itemObject : itemObjs) {
			TagNode itemNode = (TagNode)itemObject;
			Object[] h3objs = itemNode.evaluateXPath("/h3");
			if(h3objs!=null && h3objs.length>0){
				JSONObject h3jsonObj = new JSONObject();
				TagNode h3Node = (TagNode)h3objs[0];
				h3jsonObj.put("name", h3Node.getText().toString());
				h3jsonObj.put("value", "");
				specArray.put(h3jsonObj);
			}
			Object[] dtobjs = itemNode.evaluateXPath("/dl/dt");
			Object[] ddobjs = itemNode.evaluateXPath("/dl/dd");
			if(dtobjs!=null && dtobjs.length>0 && ddobjs!=null && ddobjs.length>0){
				for (int i = 0; i < dtobjs.length; i++) {
					JSONObject dtddjsonObj = new JSONObject();
					TagNode dtNode = (TagNode)dtobjs[i];
					TagNode ddNode = (TagNode)ddobjs[i];
					dtddjsonObj.put("name", dtNode.getText().toString());
					dtddjsonObj.put("value", ddNode.getText().toString());
					specArray.put(dtddjsonObj);
				}
			}
		}
		return specArray;
	}

	public String getPrice(Page page) {
		String url = page.getUrl();
		Pattern pattern = Pattern.compile("http://item.jd.com/([0-9]+).html");
		Matcher matcher = pattern.matcher(url);
		String goodsId = null;
		if(matcher.find()){
			goodsId = matcher.group(1);
			page.setGoodsid(RevUtils.reverse(goodsId)+"_jd");
		}
		String content = PageUtils.getContent("http://p.3.cn/prices/mgets?skuIds=J_"+goodsId);
		JSONArray jsonArray = new JSONArray(content);
		JSONObject jsonObject = jsonArray.getJSONObject(0);
		String price = jsonObject.getString("p");
		return price;
	}

}
