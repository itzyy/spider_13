package cn.crxy.spider_13;

import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.crxy.spider_13.domain.Page;
import cn.crxy.spider_13.download.Downloadable;
import cn.crxy.spider_13.download.HttpClientDownloadableImpl;
import cn.crxy.spider_13.process.JdProcessableImpl;
import cn.crxy.spider_13.process.Processable;
import cn.crxy.spider_13.repository.QueueRepository;
import cn.crxy.spider_13.repository.Repository;
import cn.crxy.spider_13.store.ConsoleStoreableImpl;
import cn.crxy.spider_13.store.HbaseStoreableImpl;
import cn.crxy.spider_13.store.Storeable;
import cn.crxy.spider_13.utils.Config;
import cn.crxy.spider_13.utils.SleepUtils;

public class Spider {
	private Logger logger = LoggerFactory.getLogger(Spider.class);
	private Downloadable downloadable = new HttpClientDownloadableImpl();
	private Processable processable;
	private Storeable storeable = new ConsoleStoreableImpl();
	private Repository repository = new QueueRepository();
	//创建一个具有五个线程的线程池
	private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(Config.nThread);
	
	
	public Spider() {
		String connectString = "192.168.1.171:2181,192.168.1.172:2181,192.168.1.173:2181";
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		int sessionTimeoutMs = 5000;//连接失效时间，默认是40s 这个值必须在4s-40s之间
		int connectionTimeoutMs = 3000;//连接超时时间
		//获取zk连接
		CuratorFramework client = CuratorFrameworkFactory.newClient(connectString, sessionTimeoutMs, connectionTimeoutMs, retryPolicy);
		//开启连接
		client.start();
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			String ip = localHost.getHostAddress();
			client.create()//创建节点
				.creatingParentsIfNeeded()//如果父节点不存在，则创建
				.withMode(CreateMode.EPHEMERAL)//节点类型：临时节点
				.withACL(Ids.OPEN_ACL_UNSAFE)//节点权限
				.forPath("/spider/"+ip);//节点名称
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 启动爬虫
	 */
	public void start() {
		check();
		logger.info("爬虫开始运行");
		//爬虫是一个一直运行的程序
		while(true){
			final String url = repository.poll();
			if(StringUtils.isNotBlank(url)){
				//获取一个线程去执行代码
				fixedThreadPool.execute(new Runnable() {
					public void run() {
						Page page = Spider.this.download(url);
						Spider.this.process(page);
						List<String> urls = page.getUrls();
						for (String nextUrl : urls) {
							Spider.this.repository.add(nextUrl);
						}
						if (url.startsWith("http://item.jd.com/")) {
							Spider.this.store(page);
						}
						SleepUtils.sleep(Config.million_1);
					}
				});
			}else{
				System.out.println("没有url了...");
				SleepUtils.sleep(Config.million_5);
			}
		}
	}
	
	/**
	 * 启动前的检查
	 */
	private void check() {
		logger.info("开始执行配置检查");
		if(processable==null){
			String message = "需要设置解析类...";
			logger.error(message);
			throw new RuntimeException(message);
		}
		logger.info("==========================配置检查开始===============================");
		logger.info("downloadable的实现类是：{}",downloadable.getClass().getName());
		logger.info("processable的实现类是：{}",processable.getClass().getName());
		logger.info("storeable的实现类是：{}",storeable.getClass().getName());
		logger.info("repository的实现类是：{}",repository.getClass().getName());
		logger.info("===========================配置检查结束==============================");
	}

	/**
	 * 下载url
	 * @param url 
	 */
	public Page download(String url) {
		return this.downloadable.download(url);
	}
	
	/**
	 * 解析内容
	 * 
	 * 注意：下面的xpath，第一个是正确的，第二个是错误的，不可用
	 * //*[@id="article_details"]/div[1]/h1/span/a 有效的xpath
	 * /html/body/div[5]/div/div[2]/div[1] 无效的xpath
	 * @param page 
	 */
	public void process(Page page) {
		this.processable.process(page);
	}
	
	
	/**
	 * 入库存储
	 * @param page 
	 */
	public void store(Page page) {
		this.storeable.store(page);
	}

	public void setDownloadable(Downloadable downloadable) {
		this.downloadable = downloadable;
	}

	public void setProcessable(Processable processable) {
		this.processable = processable;
	}

	public void setStroreable(Storeable stroreable) {
		this.storeable = stroreable;
	}
	
	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public void setSeedUrl(String url){
		this.repository.add(url);
	}
	
	public static void main(String[] args) {
		Spider spider = new Spider();
		spider.setProcessable(new JdProcessableImpl());
		spider.setStroreable(new HbaseStoreableImpl());
		
		String url = "http://list.jd.com/list.html?cat=9987,653,655";
		spider.setSeedUrl(url);
		spider.start();
	}

}
