package t.backstage.models.entitys;

import java.util.Date;

import javax.persistence.Table;

import t.sql.interfaces.DTO;

/***
 * 用来管理菜单权限
 * @author zhangj
 * @date 2018年9月3日 上午9:35:24
 * @email zhangjin0908@hotmail.com
 */
@Table(name="t_menu_authority")
public class TMenuAuthority implements DTO{

	private static final long serialVersionUID = -2228241165530319L;
	// 用户唯一ID
	@javax.persistence.Id
	@javax.persistence.Column
	private String id;
	// 所属角色ID
	@javax.persistence.Column
	private String rId;
	// 所属菜单ID
	@javax.persistence.Column
	private String mId;
	// 当前可用状态 1.0表示正常 0.0表示禁止
	@javax.persistence.Column
	private double state;
	// 创建人
	@javax.persistence.Column
	private String createUser;
	// 创建时间
	@javax.persistence.Column
	private Date   createTime;
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
	public String getmId() {
		return mId;
	}
	public void setmId(String mId) {
		this.mId = mId;
	}
	public double getState() {
		return state;
	}
	public void setState(double state) {
		this.state = state;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	
}

