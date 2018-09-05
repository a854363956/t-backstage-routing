package t.backstage.models.controls;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import t.backstage.models.entitys.TRequestAuthority;
import t.backstage.models.entitys.TRequestData;
import t.backstage.routing.annotations.Post;
import t.sql.Session;
import t.sql.interfaces.DTO;
import t.sql.query.Query;

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
	
	/**
	 * 根据当前id来更新数据url 以及remarks
	 */
	@Post
	public void updateRoute(JSONObject parame) {
		String id      = parame.getString("id");
		String url     = parame.getString("url");
		String remarks = parame.getString("remarks");
		sessionFactory.getCurrentSession().nativeDMLSQL("update t_request_data set url=?,remarks=? where id=? ",url,remarks,id);
	}
	/**
	 * 删除当前路由信息
	 * @param parame
	 */
	@Post
	public void delRouteAuthority(JSONObject parame) {
		String id = parame.getString("id");
		if(t.backstage.models.context.StringUtils.isNull(id)) {
			throw new t.backstage.error.BusinessException(5015,"id");
		}
		sessionFactory.getCurrentSession().nativeDMLSQL("delete from t_request_authority where id=?",id);
	}
	/**
	 * 添加路由权限
	 * @param parame
	 */
	@Post
	public void addRouteAuthority(JSONObject parame) {
		// JSON数组格式如下 { ids:['','',''],mId:''}
		JSONArray ids = parame.getJSONArray("ids");
		if(ids == null) {
		   throw new t.backstage.error.BusinessException(5015,"ids");
		}
		String    mId = parame.getString("mId");
		if(mId == null) {
			throw new t.backstage.error.BusinessException(5015,"mId");
		}
		Session session = sessionFactory.getCurrentSession();
		List<DTO> saveList = new java.util.ArrayList<DTO>();
		
		Query<TRequestAuthority> qtaQuery = session.createQuery("select * from t_request_authority where mId=:mId",TRequestAuthority.class);
		qtaQuery.setParameter("mId",mId);
		List<TRequestAuthority> qtaList = qtaQuery.list();
		
		
		for(int i=0; i < ids.size(); i+=1) {
			String id = ids.getString(i);
			List<TRequestAuthority> getHaveQta = qtaList.stream().filter(qta-> qta.getRequestId().equals(id)).collect(java.util.stream.Collectors.toList());
			if(getHaveQta.size() == 0) {
				TRequestAuthority tra = new TRequestAuthority();
				tra.setCreateTime(new Date());
				tra.setCreateUser(t.backstage.models.context.ContextUtils.getCurrentUser().getId());
				tra.setId(t.backstage.models.context.ContextUtils.getUUID());
				tra.setmId(mId);
				tra.setRequestId(id);
				tra.setState(1.0);
				saveList.add(tra);
			}
		}
		session.transactionVoid(()->{
			session.createBatch(saveList);
		});
	}
}

