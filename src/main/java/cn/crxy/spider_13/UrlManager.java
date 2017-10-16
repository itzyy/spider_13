package cn.crxy.spider_13;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

/**
 * 负责url调度
 * 每天凌晨向url仓库中添加入口url
 * @author Administrator
 *
 */
public class UrlManager {
	
	public static void main(String[] args) {
		try {
			//获取默认调度器
			Scheduler defaultScheduler = StdSchedulerFactory.getDefaultScheduler();
			//开启调度器
			defaultScheduler.start();
			
			//任务
			JobDetail jobDetail = new JobDetail("url_job", Scheduler.DEFAULT_GROUP, UrlJob.class);
			//触发时间 凌晨一点
			Trigger trigger = new CronTrigger("url_job", Scheduler.DEFAULT_GROUP, "0 0 1 * * ?");
			//添加调度任务和触发时间
			defaultScheduler.scheduleJob(jobDetail, trigger);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
}
