package t.backstage.routing.filters;

import com.alibaba.fastjson.JSONObject;

import t.backstage.models.entitys.TRequestData;
import t.sql.query.Query;

/***
 * 登入请求拦截器-  t_request_data 表的数据可以缓存在中间件
 * @author zhangj
 * @date 2018年9月10日 下午10:12:24
 * @email zhangjin0908@hotmail.com
 */
@org.springframework.stereotype.Service
public class TLoginFilter implements TFilter{
	
	@org.springframework.beans.factory.annotation.Autowired
	private t.sql.SessionFactory sessionFactory;
	
	/**
	 * 只有状态等于1的才允许在未登入的情况下进行数据访问
	 */
	@Override
	public boolean request(String path,JSONObject j) {
		Query<TRequestData> trQuery= sessionFactory.getCurrentSession().createQuery("select * from t_request_data where url=:url ",t.backstage.models.entitys.TRequestData.class);
		trQuery.setParameter("url",path);
		TRequestData trd = trQuery.uniqueResult();
		if(t.backstage.models.context.ContextUtils.getCurrentSession() == null) {
			if(trd == null) {
				return false;
			}else if(trd.getLoginAuthorize() == 0) {
				return false;
			}else if(trd.getLoginAuthorize() == 1) {
				return true;
			}else {
				return false;
			}
		}else {
			return true;
		}
	}
}

