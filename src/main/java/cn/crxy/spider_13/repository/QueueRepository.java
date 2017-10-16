package cn.crxy.spider_13.repository;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class QueueRepository implements Repository {
	private Queue<String> queue = new ConcurrentLinkedDeque<String>();
	@Override
	public String poll() {
		return queue.poll();
	}

	@Override
	public void add(String nextUrl) {
		this.queue.add(nextUrl);
	}

}
