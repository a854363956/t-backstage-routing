package t.backstage.models.jobs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/***
 * 测试QuartzFactory是否有效
 * 
 * @author zhangj
 * @date 2018年9月12日 上午8:49:07
 * @email zhangjin0908@hotmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:t/backstage/config/spring/spring-main.xml" })
public class TestQuartzFactory {
	
	/**
	 * 添加Job实例
	 **/
	@Test
	public void testAddJob() throws SchedulerException, InterruptedException {
		String triggerName = "test12";
		String triggerGroupName = "test2";
		QuartzFactory qf = QuartzFactory.singleCase();
		JobDetail jobDetail = JobBuilder.newJob(TestJob.class).storeDurably().withIdentity(triggerName, triggerGroupName).build();
		TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
		triggerBuilder.withIdentity(triggerName, triggerGroupName);
		triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule("*/5 * * * * ?"));
		triggerBuilder.startNow();
		CronTrigger trigger = (CronTrigger) triggerBuilder.build();
		qf.getScheduler().scheduleJob(jobDetail, trigger);
		while (true) {
			Thread.sleep(2000);
		}
	}
	
	/**
	 * 删除Job实列
	 * @throws SchedulerException
	 */
	@Test
	public void delAddJob() throws SchedulerException {
		String triggerName = "test12";
		String triggerGroupName = "test2";
		QuartzFactory quartzFactory = QuartzFactory.singleCase();
		Scheduler sched = quartzFactory.getScheduler();  
       TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
       sched.pauseTrigger(triggerKey); 
       sched.unscheduleJob(triggerKey);  
       sched.deleteJob(JobKey.jobKey(triggerName, triggerGroupName)); 
	}
	
}