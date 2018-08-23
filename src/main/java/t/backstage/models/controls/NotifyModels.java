package t.backstage.models.controls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import t.backstage.models.entitys.TNotifySubscription;
import t.sql.query.Query;

/***
 * 用来做消息提示的Models
 * @author zhangj
 * @date 2018年8月23日 下午1:52:57
 * @email zhangjin0908@hotmail.com
 */
@org.springframework.stereotype.Service
public class NotifyModels {
	@org.springframework.beans.factory.annotation.Autowired
	private t.sql.SessionFactory sessionFactory;
	/**
	 * 用来获取当前人员的消息提醒数量
	 * @param j
	 * @return
	 */
	@t.backstage.routing.annotations.Post
	public Object notifyCount() {
		String sql = "select count(1) as notifyCount  from t_base_notify  t where t.msgType in :msgType ";
		Query<Map<String,Object>> tQuery = sessionFactory.getCurrentSession().createQuery(sql,HashMap.class);
		tQuery.setParameter("msgType",getSubscription());
		return tQuery.uniqueResult().get(t.backstage.models.context.DBMapKey.notifyCount);
	}
	
	/**
	 * 获取当前订阅的信息
	 * @return
	 */
	public List<Integer> getSubscription(){
		String rId = t.backstage.models.context.ContextUtils.getCurrentRole().getId();
		String sql = "select * from t_notify_subscription where rId = :rId";
		Query<TNotifySubscription> tQuery = sessionFactory.getCurrentSession().createQuery(sql,TNotifySubscription.class);
		tQuery.setParameter("rId",rId);
		List<Integer> typeMsg = new ArrayList<Integer>();
		tQuery.list().forEach(tns->{
			typeMsg.add(tns.getMsgType());
		});
		return typeMsg;
	}
}

