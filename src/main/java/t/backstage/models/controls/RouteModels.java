package t.backstage.models.controls;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;

import t.backstage.models.entitys.TRequestData;
import t.backstage.routing.annotations.Post;
import t.sql.Session;

/***
 * 路由信息维护
 * @author zhangj
 * @date 2018年9月4日 上午10:29:21
 * @email zhangjin0908@hotmail.com
 */
@org.springframework.stereotype.Service
public class RouteModels {
	@org.springframework.beans.factory.annotation.Autowired
	private t.sql.SessionFactory sessionFactory;
	
	/**
	 * 添加路由信息到路由的基础表
	 * @param parame
	 * @throws NoSuchAlgorithmException
	 */
	@Post
	public void addRoute(JSONObject parame) throws NoSuchAlgorithmException {
		// 路由的url地址 
		String url     = parame.getString("url");
		// 备注信息描述
		String remarks = parame.getString("remarks");
		
		TRequestData trd = new TRequestData();
		trd.setId(t.backstage.models.context.ContextUtils.getUUID());
		trd.setUrl(url);
		trd.setRemarks(remarks);
		trd.setCreateTime(new Date());
		trd.setCreateUser(t.backstage.models.context.ContextUtils.getCurrentUser().getId());
		sessionFactory.getCurrentSession().create(trd);
	}
	
	/***
	 * 根据ID删除当前的路由信息
	 * @param parame 
	 *            -id 当前数据的id
	 */
	@Post
	public void delRoute(JSONObject parame) {
		// 当前的id
		String id  = parame.getString("id");
		Session session = sessionFactory.getCurrentSession();
		session.transactionVoid(()->{
			session.nativeDMLSQL("delete from t_request_authority where requestId = ?",id);
			session.nativeDMLSQL("delete from t_request_data where id = ?",id);
		});
	}
}

