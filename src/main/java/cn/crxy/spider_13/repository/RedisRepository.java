package cn.crxy.spider_13.repository;

import cn.crxy.spider_13.utils.RedisUtils;

public class RedisRepository implements Repository{

	RedisUtils redisUtils = new RedisUtils();
	@Override
	public String poll() {
		return redisUtils.poll(RedisUtils.key);
	}

	@Override
	public void add(String nextUrl) {
		redisUtils.add(RedisUtils.key, nextUrl);
	}

}
