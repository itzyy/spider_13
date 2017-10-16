package cn.crxy.spider_13.repository;

import java.util.HashMap;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;

import cn.crxy.spider_13.utils.DomainUtils;
/**
 * 这个思路没有问题，但是hashmap和queu都不共享，分布式下使用会有问题
 * 改造思路：
 * 使用redis中的hash类型和list类型进行改造
 * @author Administrator
 *
 */
public class RandomQueueRepository implements Repository {

	HashMap<String, Queue<String>> hashMap = new HashMap<String, Queue<String>>();
	
	Random random = new Random();
	@Override
	public String poll() {
		String[] keys = hashMap.keySet().toArray(new String[hashMap.size()]);
		//随机获取一个数字，这个数字就是数组的角标
		int nextInt = random.nextInt(keys.length);
		//获取一个随机的key
		String key = keys[nextInt];
		Queue<String> queue = hashMap.get(key);
		return queue.poll();
	}

	@Override
	public void add(String nextUrl) {
		//获取顶级域名
		String topDomain = DomainUtils.getTopDomain(nextUrl);
		Queue<String> queue = hashMap.get(topDomain);
		if(queue==null){
			queue=new ConcurrentLinkedDeque<String>();
		}
		queue.add(nextUrl);
		hashMap.put(topDomain, queue);
	}

}
