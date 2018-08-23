package t.backstage.models.controls;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import t.backstage.models.entitys.TBaseNotify;
import t.backstage.models.entitys.TNotifySubscription;
import t.backstage.routing.annotations.Post;
import t.sql.interfaces.DTO;
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
	private SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	/**
	 * 用来获取当前人员的消息提醒数量
	 * @param j
	 * @return
	 */
	public Object notifyCount() {
		String sql = "select count(1) as notifyCount  from t_base_notify  t where t.msgType in :msgType and t.state = 0.00 ";
		Query<Map<String,Object>> tQuery = sessionFactory.getCurrentSession().createQuery(sql,HashMap.class);
		tQuery.setParameter("msgType",getSubscription());
		return tQuery.uniqueResult().get(t.backstage.models.context.DBMapKey.notifyCount);
	}
	/**
	 * 获取当前中所有订阅的消息
	 * @param j
	 * @return
	 */
	@Post
	public List<Notify> getNotifyAll(JSONObject j){
		String sql ="select  *  from t_base_notify  t where t.msgType in :msgType  and t.state = 0.00";
		Query<TBaseNotify> tQuery = sessionFactory.getCurrentSession().createQuery(sql,TBaseNotify.class);
		tQuery.setParameter("msgType",getSubscription());
		List<Notify> result = new ArrayList<Notify>();
		tQuery.list().forEach(tbn->{
			Notify n = new Notify();
			n.setTitle(tbn.getTitle());
			n.setDatetime(sdf.format(tbn.getCreateTime()));
			n.setId(tbn.getId());
			n.setDescription(tbn.getContent());
			n.setType(getMsgTypeToText(tbn.getMsgType()));
			n.setAvatar(tbn.getAvatar());
			result.add(n);
		});
		return result;
	}
	
	/**
	 * 清除当前的订阅消息
	 * @param j
	 */
	@Post
	public void clearNotices(JSONObject j) {
		String sql ="select  *  from t_base_notify  t where t.msgType in :msgType  and t.state = 0.00";
		Query<DTO> tQuery = sessionFactory.getCurrentSession().createQuery(sql,TBaseNotify.class);
		tQuery.setParameter("msgType",getSubscription());
		Collection<DTO> re  = tQuery.list();
		re.forEach(tbn->{
			((TBaseNotify)tbn).setState(1);
		});
		sessionFactory.getCurrentSession().updateBatch(re);
	}
	public String getMsgTypeToText(int type) {
		return type == 0?"通知":type==1?"消息":type==2?"待办":"未知类型["+type+"]";
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
/**
 * 当前返回给前端的消息类型
 * @author zhangj
 */
class Notify{
	// 通知ID
	private String id;
	// 头像地址
	private String avatar;
	// 消息标题
	private String title;
	// 消息描述
	private String description;
	// 时间
	private String datetime;
	// 消息类型
	private String type;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}

