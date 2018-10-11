package t.backstage.models.entitys;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import t.sql.interfaces.DTO;

/***
 * 用户所属的仓库
 * @author zhangj
 * @date 2018年10月11日 下午4:05:56
 * @email zhangjin0908@hotmail.com
 */
public class TBaseWarehouse implements DTO{

	private static final long serialVersionUID = -7973244594315857813L;

	/**
	 * 用户唯一ID
	 */
	@Id
	@Column
	private long id;
	
	/**
	 * 仓库名称
	 */
	@Column
	private String name;
	/**
	 * 创建时间
	 */
	@Column
	private Date createTime;
	/**
	 * 失效时间
	 */
	@Column
	private Date effectiveDate;
	/**
	 * 状态
	 */
	@Column
	private double state;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	public double getState() {
		return state;
	}
	public void setState(double state) {
		this.state = state;
	}
	
}

