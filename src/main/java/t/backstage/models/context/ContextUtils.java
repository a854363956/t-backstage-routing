package t.backstage.models.context;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;

import t.backstage.models.entitys.TBaseLanguage;
import t.backstage.models.entitys.TBaseRole;
import t.backstage.models.entitys.TBaseSession;
import t.backstage.models.entitys.TBaseUser;
import t.sql.Session;
import t.sql.query.Query;

/***
 * 当前全局的工具类
 * @author zhangj
 * @date 2018年8月20日 下午1:43:28
 * @email zhangjin0908@hotmail.com
 */
public class ContextUtils {
	/**
	 * 获取当前请求的Token 
	 * @param request 当前请求的JSON数据
	 * @return 返回当前JSON数据中的token
	 */
	public static String getToken(JSONObject request) {
		return request.getString("token");
	}
	/**
	 * 获取当前的HttpServletRequest对象,仅仅只能在当前线程中获取
	 * @return 返回HttpServletRequest对象
	 */
	public static HttpServletRequest getHttpServletRequest() {
		return t.backstage.routing.TService.getThreadRequest();
	}
	/**
	 * 获取当前的HttpServletResponse对象,仅仅只能在当前线程中获取
	 * @return 返回HttpServletResponse对象
	 */
	public static HttpServletResponse getHttpServletResponse() {
		return t.backstage.routing.TService.getThreadResponse();
	}
	
	/**
	 * 获取系统最底层的异常信息
	 * @param e
	 * @return
	 */
	public static Throwable getBottomError(Throwable e) {
		Throwable ex = e.getCause();
		if (ex == null) {
			return e;
		} else {
			return getBottomError(ex);
		}
	}

	/**
	 * 根据代号获取相关的消息
	 * @param code
	 * @return
	 */
	public static String getLanguage(int code) {
		t.sql.SessionFactory sf = SpringUtils.getApplicationContext().getBean(t.sql.SessionFactory.class);
		Query<TBaseLanguage> query = sf.getCurrentSession().createQuery("select * from t_base_language where code = :code",t.backstage.models.entitys.TBaseLanguage.class);
		query.setParameter("code",code);
		List<TBaseLanguage> list =  query.list();
		if(list.size() == 0) {
			throw new t.backstage.error.ServiceException("The corresponding data is not maintained, table name is 't_base_language' and code is '"+code+"'");
		}
		
		String label = "";
		if(list.size() == 1) {
			label = list.get(0).getLabel();
			return label;
		}
		for(TBaseLanguage tbl : list) {
			int language = 0;
			//  修复当前如果没有登入人则无法获取多语言的支持  -START
			if(getCurrentUser() != null) {
				language = getCurrentUser().getLanguage();
			}
			// END 
			if(tbl.getType() == language) {
				label = tbl.getLabel();
			}
		}
		return label;
	}
	/**
	 * 进行取字符串HASH
	 * @param text
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String md5(String text) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update((text).getBytes());
		String result = "00000000000000000000000000000000" + new BigInteger(1, md.digest()).toString(16);
		return result.substring(result.length() - 32, result.length());
	}
	/**
	 * 获取当前系统的IP地址
	 * @return
	 */
	public static String getIpAddr() {
		HttpServletRequest request = getHttpServletRequest();
		return request.getRemoteAddr();
	}
	/**
	 * 获取唯一的UUID
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String getUUID()  {
		try {
			return md5(java.util.UUID.randomUUID().toString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 获取当前的Session对象
	 * @return 返回当前的Session对象
	 */
	public static TBaseSession getCurrentSession() {
		Session session = SpringUtils.getApplicationContext().getBean(t.sql.SessionFactory.class).getCurrentSession();
		Query<TBaseSession> query = session.createQuery("select * from t_base_session where id =:id",TBaseSession.class);
		query.setParameter("id",t.backstage.routing.TService.getThreadToken());
		return query.uniqueResult();
	}
	/**
	 * 获取当前的用户对象
	 * @return  返回当前的用户对象
	 */
	public static TBaseUser  getCurrentUser() {
		Session session = SpringUtils.getApplicationContext().getBean(t.sql.SessionFactory.class).getCurrentSession(); 
		Query<TBaseUser> query = session.createQuery("select * from t_base_user where id=:id",TBaseUser.class);
		query.setParameter("id",getCurrentSession().getuId());
		return query.uniqueResult();
	} 
	/**
	 * 获取当前用户的角色信息
	 * @return  返回当前的角色信息
	 */
	public static TBaseRole getCurrentRole() {
		Session session = SpringUtils.getApplicationContext().getBean(t.sql.SessionFactory.class).getCurrentSession(); 
		Query<TBaseRole> query = session.createQuery("select * from t_base_role where id=:id",TBaseRole.class);
		query.setParameter("id",getCurrentUser().getrId());
		return query.uniqueResult();
	}
}

