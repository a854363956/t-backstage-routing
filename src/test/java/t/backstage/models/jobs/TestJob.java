package t.backstage.models.jobs;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/***
 * 
 * @author zhangj
 * @date 2018年9月12日 上午8:55:39
 * @email zhangjin0908@hotmail.com
 */
public class TestJob implements org.quartz.Job{
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println(1);
	}

}

