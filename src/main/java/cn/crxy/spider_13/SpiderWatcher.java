package cn.crxy.spider_13;

import java.util.List;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
/**
 * 监视器需要一直运行
 * @author Administrator
 *
 */
public class SpiderWatcher implements Watcher {
	CuratorFramework client;
	List<String> childrenList;
	public SpiderWatcher(){
		String connectString = "192.168.1.171:2181,192.168.1.172:2181,192.168.1.173:2181";
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		int sessionTimeoutMs = 5000;//连接失效时间，默认是40s 这个值必须在4s-40s之间
		int connectionTimeoutMs = 3000;//连接超时时间
		//获取zk连接
		client = CuratorFrameworkFactory.newClient(connectString, sessionTimeoutMs, connectionTimeoutMs, retryPolicy);
		//开启连接
		client.start();
		
		try {
			//注册监视器 监控spider节点下面所有子节点的变化情况(这个监视器只能使用一次，需要重复注册)
			childrenList = client.getChildren().usingWatcher(this).forPath("/spider");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void process(WatchedEvent event) {
		try {
			//重新注册监视器
			List<String> newChildrenList = client.getChildren().usingWatcher(this).forPath("/spider");
			for (String node : childrenList) {
				if(!newChildrenList.contains(node)){
					System.out.println("节点消失："+node);
					//TODO-- 需要给管理员发送邮件或者短信或者打电话
					/**
					 * 发邮件：javamail
					 * 发短信：云片网
					 */
				}
			}
			
			for (String node : newChildrenList) {
				if(!childrenList.contains(node)){
					System.out.println("新增节点："+node);
				}
			}
			childrenList = newChildrenList;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		SpiderWatcher spiderWatcher = new SpiderWatcher();
		spiderWatcher.start();
	}

	private void start() {
		while(true){
			;
		}
	}

}
