package t.backstage.routing;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.quartz.SchedulerException;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import t.backstage.models.context.SpringUtils;
import t.backstage.models.jobs.QuartzFactory;
import t.backstage.routing.annotations.Post;
import t.backstage.routing.filters.TFilter;



/***
 * 用来处理通用的请求的入口
 * @author zhangj
 * @date 2018年8月7日 上午11:09:36
 * @email zhangjin0908@hotmail.com
 */
public class TService extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private static ThreadLocal<HttpServletRequest> threadRequest = new ThreadLocal<HttpServletRequest>();
	private static ThreadLocal<HttpServletResponse> threadResponse = new ThreadLocal<HttpServletResponse>();
	private static ThreadLocal<String> threadToken = new ThreadLocal<String>(); 
	private static QuartzFactory qf;
	
	// AES对前端请求的数据加密的密钥
	private static String KEY = "2ea84ab4b6df474a961cfb2300cea7db";
	
	// 用户如果时间超过5秒钟就认为是重放攻击 单位/毫秒
	private static long  PREVENT_REPLAY_ATTACK_TIME = 5000;
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendError(404);
	}

	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendError(404);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendError(404);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendError(404);
	}

	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendError(404);
	}

	@Override
	protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendError(404);
	}

	/**
	 * 针对Post请求进行路由
	 */
	@Override 
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		threadRequest.set(req);
		threadResponse.set(resp);
		// 当前请求的路径
		OutputStream out = resp.getOutputStream();
		java.io.InputStream in = req.getInputStream();
		String path = req.getPathInfo();
		try {
			if (t.sql.utils.StringUtils.findRegular(path, "^/[a-zA-Z0-9_]+/[a-zA-Z0-9_]+$").size() == 0) {
				throw new t.backstage.error.ServiceException("Invalid request path!");
			}else {
				String[] paths = path.split("/");
				String clzz = paths[1];
				String method = paths[2];
				Object invObject = t.backstage.models.context.SpringUtils.getApplicationContext(req.getSession().getServletContext()).getBean(clzz);
				// 暂时只是支持JSONObject类型,不支持其他实体类型 START
				Method met =invObject.getClass().getDeclaredMethod(method,com.alibaba.fastjson.JSONObject.class);
				// END
				Post post = met.getAnnotation(t.backstage.routing.annotations.Post.class);
				if(post != null) {
					String body = t.backstage.models.context.CryptoJS.AES.decrypt(t.backstage.models.context.IOUtils.readInputStream(in).replaceAll("\"",""),KEY);
					JSONObject jBody = com.alibaba.fastjson.JSON.parseObject(body);
					threadToken.set(jBody.getString("token"));
					long localSendTime = jBody.getLong("localSendTime");
					Date cureeDate  = new Date();
					// 如果当前发送的时候大于5秒那么就认为是超时，在进行重放攻击 一般情况下执行到此刻的时间不会超过5000毫秒
					if(localSendTime + PREVENT_REPLAY_ATTACK_TIME < cureeDate.getTime()) {
						throw new RuntimeException("Request has expired, please request again !");
					}
					// 此处用来校验的路径拦截器
					String[] beanResult = t.backstage.models.context.SpringUtils.getApplicationContext().getBeanNamesForType(t.backstage.routing.filters.TFilter.class);
					for(String beanName : beanResult) {
						TFilter t = (TFilter) SpringUtils.getApplicationContext().getBean(beanName);
						boolean rBoolean = t.request(path,com.alibaba.fastjson.JSON.parseObject(body));
						if(!rBoolean) {
							throw new t.backstage.error.BusinessException(5016,path);	
						}
					}
					
					Type[] paramTypeList = met.getGenericParameterTypes();
					Map<String,Object> result = new HashMap<String,Object>();
					if(paramTypeList.length == 0) {
						Object o = met.invoke(invObject);
						if(o == null) {
							result.put("datas","");
						}else {
							result.put("datas",o);
						}
						result.put("status",0);
						out.write(com.alibaba.fastjson.JSON.toJSONString(result).getBytes("UTF-8"));
						return;
					}else if(paramTypeList.length == 1) {
						@SuppressWarnings("rawtypes")
						Class cl =Class.forName(paramTypeList[0].getTypeName());
						Object o  = null;
						if(com.alibaba.fastjson.JSONObject.class.getName().equals(cl.getName())) {
							o= met.invoke(invObject,com.alibaba.fastjson.JSON.parseObject(body));
						}else if(String.class.getName().equals(cl.getName())) {
							o= met.invoke(invObject,body);
						}else {
							throw new t.backstage.error.ServiceException("Unsupported type,URL is ["+path+"] type is ["+cl.getName()+"]");
						}
						if(o == null) {
							result.put("datas","");
						}else {
							result.put("datas",o);
						}
						//  0表示请求成功 -1表示请求失败
						result.put("status",0);
						// 修复long整数对浏览器导致的不精准的问题
						out.write(com.alibaba.fastjson.JSON.toJSONString(result,SerializerFeature.BrowserCompatible).getBytes("UTF-8"));
						return;
					}else {
						throw new t.backstage.error.ServiceException("Multi-parameters are not supported,URL is ["+path+"]");
					}
				}else {
					throw new t.backstage.error.ServiceException("No annotations prohibit access!");
				}
			}
		} catch (Exception e) {
			// 精简异常信息,将其他无关的异常信息去掉
			Throwable ex = t.backstage.models.context.ContextUtils.getBottomError(e);
			ex.printStackTrace();
			Map<String,Object> result = new HashMap<String,Object>(); 
		    //  0表示请求成功 -1表示请求失败
			result.put("status",-1);
			result.put("datas",t.backstage.models.context.ContextUtils.getBottomError(e).getMessage());
			out.write(com.alibaba.fastjson.JSON.toJSONString(result).getBytes("UTF-8"));
		} finally {
			out.flush();
			out.close();
		}
	}
	
	/**
	 * 当前启动Tomcat的Service的时候加载定时器
	 */
	@Override
	public void init() throws ServletException {
		try {
			QuartzFactory qf = t.backstage.models.jobs.QuartzFactory.singleCase();
			this.qf = qf;
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	// 注意只在同一个线程中有效
	public static HttpServletRequest getThreadRequest() {
		return threadRequest.get();
	}
	// 注意只在同一个线程中有效
	public static HttpServletResponse getThreadResponse() {
		return threadResponse.get();
	}
	// 注意只在同一个线程中有效
	public static String getThreadToken() {
		return threadToken.get();
	}
	// 定时器全局有效
	public static QuartzFactory getQuartzFactory() {
		return qf;
	}


	
}

