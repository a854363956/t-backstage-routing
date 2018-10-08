package t.backstage.models.entitys;

import java.util.Date;

import javax.persistence.Table;

import t.sql.interfaces.DTO;

/***
 * 路由请求的权限管理
 * @author zhangj
 * @date 2018年9月5日 上午10:02:05
 * @email zhangjin0908@hotmail.com
 */
@Table(name="t_request_authority")
public class TRequestAuthority  implements DTO{
	private static final long serialVersionUID = -298278621942561975L;
	// 用户唯一ID
	@javax.persistence.Column
	private long id;
	// 用户请求的路由ID
	@javax.persistence.Column
	private String requestId;
	// 用户的菜单ID
	@javax.persistence.Column
	private long mId;
	// 当前路由的状态
	@javax.persistence.Column
	private double state; 
	// 当前创建人
	@javax.persistence.Column
	private long createUser;
	// 当前创建时间
	@javax.persistence.Column
	private Date   createTime;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	
	public long getmId() {
		return mId;
	}
	public void setmId(long mId) {
		this.mId = mId;
	}
	public double getState() {
		return state;
	}
	public void setState(double state) {
		this.state = state;
	}
	public long getCreateUser() {
		return createUser;
	}
	public void setCreateUser(long createUser) {
		this.createUser = createUser;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	
}

