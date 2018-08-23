package t.backstage.models.entitys;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;

import t.sql.interfaces.DTO;

/***
 * 获取当前通知的详细信息
 * @author zhangj
 * @date 2018年8月23日 下午4:29:27
 * @email zhangjin0908@hotmail.com
 */
@Table(name="t_base_notify")
public class TBaseNotify implements DTO{
	private static final long serialVersionUID = 8992876515942947859L;
	// 用户唯一ID
	@Id
	@javax.persistence.Column
	private String id;
	// 消息的标题
	@javax.persistence.Column
	private String title;
	// 消息的内容
	@javax.persistence.Column
	private String content;
	// 当前的状态
	@javax.persistence.Column
	private double state;
	// 消息的类型
	@javax.persistence.Column
	private int    msgType;
	// 连接打开的页面
	@javax.persistence.Column
	private String linkPath;
	// 创建时间
	@javax.persistence.Column
	private Date   createTime;
	// 创建人
	@javax.persistence.Column
	private String createUser;
	// 用户头像
	@javax.persistence.Column
	private String avatar;
	
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public double getState() {
		return state;
	}
	public void setState(double state) {
		this.state = state;
	}
	public int getMsgType() {
		return msgType;
	}
	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}
	public String getLinkPath() {
		return linkPath;
	}
	public void setLinkPath(String linkPath) {
		this.linkPath = linkPath;
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

