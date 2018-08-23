package t.backstage.models.context;

import javax.servlet.ServletContext;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

/***
 * 用来管理Spring上下文的工具类
 * @author zhangj
 * @date 2018年8月20日 下午2:01:10
 * @email zhangjin0908@hotmail.com
 */

@Component
public class SpringUtils  implements ApplicationContextAware {
	private static ApplicationContext applicationContext;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringUtils.applicationContext=applicationContext;
	}
	/**
	 * 获得Spring的上下文
	 * @return applicationContext
	 */
	public static  ApplicationContext getApplicationContext(){
		return SpringUtils.applicationContext; 
	}
	/**
	 * 获得Spring的上下文
	 * @return applicationContext
	 */
	public static  ApplicationContext getApplicationContext(ServletContext sc){
		return WebApplicationContextUtils.getWebApplicationContext(sc);
	}
}

