package t.backstage.models.entitys;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;

import t.sql.interfaces.DTO;

/***
 * 当前用户的角色信息的实体对象
 * @author zhangj
 * @date 2018年8月20日 下午5:10:07
 * @email zhangjin0908@hotmail.com
 */
@Table(name = "t_base_role")
public class TBaseRole implements DTO{
	private static final long serialVersionUID = -4442758628475561305L;
	// 用户唯一ID
	@Id
	@javax.persistence.Column
	private long id;
	// 角色名称
	@javax.persistence.Column
	private String roleName;
	// 角色创建的人
	@javax.persistence.Column
	private long createUser;
	// 当前角色创建的时间
	@javax.persistence.Column
	private Date   createTime; 
	// 仓库ID
	@javax.persistence.Column
	private long whId;
	// 当前角色状态
	@javax.persistence.Column
	private double state;
	// 是否是系统管理员 0 表示是系统管理员 1表示不是系统管理员
	@javax.persistence.Column
	private int isRoot;
	
	
	
	public int getIsRoot() {
		return isRoot;
	}
	public void setIsRoot(int isRoot) {
		this.isRoot = isRoot;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
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
	
	public long getWhId() {
		return whId;
	}
	public void setWhId(long whId) {
		this.whId = whId;
	}
	public double getState() {
		return state;
	}
	public void setState(double state) {
		this.state = state;
	}
	
}	

