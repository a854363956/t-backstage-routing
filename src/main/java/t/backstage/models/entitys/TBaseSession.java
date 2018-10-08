package t.backstage.models.entitys;

import java.util.Date;

import javax.persistence.Table;

import t.sql.interfaces.DTO;

/***
 * 用来管理用户Session对象的实体
 * @author zhangj
 * @date 2018年8月20日 下午4:35:17
 * @email zhangjin0908@hotmail.com
 */
@Table(name= "t_base_session")
public class TBaseSession implements DTO{
	private static final long serialVersionUID = -4867538905826839751L;
	// 用户唯一ID
	@javax.persistence.Id
	@javax.persistence.Column
	private String id ;
	// 人员信息关联的ID
	@javax.persistence.Column
	private long uId;
	// 用户登入方式
	@javax.persistence.Column
	private double loginMode;
	// 所属仓库,目前仅仅只是支持一个人员对应一个仓库
	@javax.persistence.Column
	private long whId;
	// 当前用户登入的ip地址
	@javax.persistence.Column
	private String ipAddr;
	// 当前用户创建的时间
	@javax.persistence.Column
	private Date   createTime;
	// 当前用户登入的设备
	@javax.persistence.Column
	private long equipmentId;
	
	

	public long getEquipmentId() {
		return equipmentId;
	}
	public void setEquipmentId(long equipmentId) {
		this.equipmentId = equipmentId;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getuId() {
		return uId;
	}
	public void setuId(long uId) {
		this.uId = uId;
	}
	public double getLoginMode() {
		return loginMode;
	}
	public void setLoginMode(double loginMode) {
		this.loginMode = loginMode;
	}
	
	public long getWhId() {
		return whId;
	}
	public void setWhId(long whId) {
		this.whId = whId;
	}
	public String getIpAddr() {
		return ipAddr;
	}
	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}

