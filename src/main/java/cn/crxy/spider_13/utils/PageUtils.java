package cn.crxy.spider_13.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageUtils {
	private static Logger logger = LoggerFactory.getLogger(PageUtils.class);
	
	/**
	 * 根据url获取页面的内容
	 * @param url
	 * @return
	 */
	public static String getContent(String url){
		//获取httpclient对象(可以认为是获取到了一个浏览器对象)
		HttpClientBuilder builder = HttpClients.custom();
		//设置浏览器信息
		builder.setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.132 Safari/537.36");
		//设置代理ip 在这代理ip不能直接写死(需要维护一个代理ip库，可以使用redis的set集合来做)，应该是从代理ip库中取一个
		String proxy_ip = "221.7.167.202";
		int proxy_port = 8123;
		HttpHost proxy = new HttpHost(proxy_ip, proxy_port);
		CloseableHttpClient client = builder/*.setProxy(proxy )*/.build();
		//疯转get请求
		HttpGet httpGet = new HttpGet(url);
		String content = null;
		try {
			long start_time = System.currentTimeMillis();
			//执行请求，获取response内容
			CloseableHttpResponse response = client.execute(httpGet);
			//获取页面实体对象
			HttpEntity entity = response.getEntity();
			//打印页面内容
			content = EntityUtils.toString(entity);//这个代码内部会关闭数据流和连接
			logger.info("页面下载成功,消耗时间:{},url:{}",System.currentTimeMillis()-start_time,url);
		}catch(HttpHostConnectException e){
			//可以在这吧失效的代理ip从本地代理ip库中删除掉，或者只记录日志，让后面的其他程序分析日志进行处理
			logger.error("代理ip失效：ip:{},port:{},url{}",proxy_ip,proxy_port,url);
		}catch (Exception e) {
			logger.error("页面下载失败,url:{}",url);
			e.printStackTrace();
		}
		return content;
	}

}
