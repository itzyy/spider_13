package cn.crxy.spider_13;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import cn.crxy.spider_13.utils.RedisUtils;

public class UrlJob implements Job {

	RedisUtils redisUtils = new RedisUtils();
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		//所有的入口地址会存储到start_url这个list队列中，每天循环从这个队列中取数据，然后添加到url仓库中
		List<String> urls = redisUtils.lrange(RedisUtils.start_url, 0, -1);
		for (String url : urls) {
			redisUtils.add(RedisUtils.key, url);
		}
	}

}
