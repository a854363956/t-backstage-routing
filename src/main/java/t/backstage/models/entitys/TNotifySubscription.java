package t.backstage.models.entitys;

import java.util.Date;

import javax.persistence.Table;

import t.sql.interfaces.DTO;

/***
 * 获取当前订阅的消息类型
 * @author zhangj
 * @date 2018年8月23日 下午1:59:24
 * @email zhangjin0908@hotmail.com
 */
@Table(name="t_notify_subscription")
public class TNotifySubscription implements DTO{
	private static final long serialVersionUID = -6163470906976940222L;
	// 唯一ID
	@javax.persistence.Id
	@javax.persistence.Column
	private String id;
	// 当前订阅的角色ID
	@javax.persistence.Column
	private String rId;
	// 当前的消息类型
	@javax.persistence.Column
	private int msgType;
	// 创建时间
	@javax.persistence.Column
	private Date createTime;
	// 创建人
	@javax.persistence.Column
	private String createUser;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getrId() {
		return rId;
	}
	public void setrId(String rId) {
		this.rId = rId;
	}
	public int getMsgType() {
		return msgType;
	}
	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	
}

