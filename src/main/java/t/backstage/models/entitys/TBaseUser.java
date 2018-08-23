package t.backstage.models.entitys;

import java.util.Date;

import javax.persistence.Table;

import t.sql.interfaces.DTO;

/***
 * 用户基础表
 * @author zhangj
 * @date 2018年8月20日 下午3:52:31
 * @email zhangjin0908@hotmail.com
 */
@Table(name="t_base_user")
public class TBaseUser implements DTO{
	private static final long serialVersionUID = 5344019789963991367L;
	// 用户唯一ID
	@javax.persistence.Id
	@javax.persistence.Column
	private String id;
	// 用户显示名称
	@javax.persistence.Column
	private String userName;
	// 用户登入名称
	@javax.persistence.Column
	private String loginName;
	// 用户的HASH值
	@javax.persistence.Column
	private String password;
	// 在某个日期之前是允许登入的-有效期
	@javax.persistence.Column
	private Date   effectiveDate;
	// 当前创建时间
	@javax.persistence.Column
	private Date   createTime;
	// 创建人
	@javax.persistence.Column
	private String createUser;
	// 当前所属仓库
	@javax.persistence.Column
	private String whId;
	// 当前人的状态
	@javax.persistence.Column
	private double state;
	// 当前所属角色
	@javax.persistence.Column
	private String rId;
	// 允许在线的最大时长单位/分钟
	@javax.persistence.Column
	private int    online;
	// 语言类型,默认为基础语言 0
	@javax.persistence.Column
	private int    language;
	// 人员头像的URI地址
	@javax.persistence.Column
	private String avatar;
	
	
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public int getLanguage() {
		return language;
	}
	public void setLanguage(int language) {
		this.language = language;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Date getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
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
	public String getWhId() {
		return whId;
	}
	public void setWhId(String whId) {
		this.whId = whId;
	}
	public double getState() {
		return state;
	}
	public void setState(double state) {
		this.state = state;
	}
	public String getrId() {
		return rId;
	}
	public void setrId(String rId) {
		this.rId = rId;
	}
	public int getOnline() {
		return online;
	}
	public void setOnline(int online) {
		this.online = online;
	}
	
}

