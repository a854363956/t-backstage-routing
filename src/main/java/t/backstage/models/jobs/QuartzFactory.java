package t.backstage.models.jobs;


import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
/**
 * Quartz工厂方法
 * @author zhangj
 * @date 2018-05-11 10:11:53
 * @email zhangjin0908@hotmail.com
 */
public class QuartzFactory {
	private Scheduler scheduler;
	private static String PATH ="/t/backstage/config/quartz/quartz.properties";
	private static QuartzFactory quartzFactory;
	private QuartzFactory() throws SchedulerException {
		StdSchedulerFactory fact = new StdSchedulerFactory();
		fact.initialize(getClass().getResourceAsStream(PATH));
		scheduler= fact.getScheduler();
		scheduler.start();
	}
	public Scheduler getScheduler() {
		return this.scheduler;
	}
	/**
	 * 单例获取初始化方法
	 * @return
	 * @throws SchedulerException
	 */
	public static QuartzFactory singleCase() throws SchedulerException {
		if(QuartzFactory.quartzFactory == null ) {
			QuartzFactory.quartzFactory = new QuartzFactory();
		}
		return QuartzFactory.quartzFactory;
	}
	
}
