package t.backstage.models.entitys;

import java.util.Date;

import t.sql.interfaces.DTO;

/***
 * 用来管理数据表格的数据源
 * @author zhangj
 * @date 2018年8月27日 上午9:23:11
 * @email zhangjin0908@hotmail.com
 */
@javax.persistence.Table(name="t_data_source")
public class TDataSource implements DTO{
	private static final long serialVersionUID = -6607115213396717089L;
	// 唯一ID
	@javax.persistence.Id
	@javax.persistence.Column
	private long id;
	// 唯一代号名称,用户用来使用的名称
	@javax.persistence.Column
	private String nameCode;
	// 唯一数据类型
	@javax.persistence.Column
	private int    dataType;
	// 脚本内容
	@javax.persistence.Column
	private String scriptContent;
	// 创建日期
	@javax.persistence.Column
	private Date   createTime;
	// 创建人
	@javax.persistence.Column
	private long createUser;
	// 备注
	@javax.persistence.Column
	private String remarks;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getNameCode() {
		return nameCode;
	}
	public void setNameCode(String nameCode) {
		this.nameCode = nameCode;
	}
	public int getDataType() {
		return dataType;
	}
	public void setDataType(int dataType) {
		this.dataType = dataType;
	}
	public String getScriptContent() {
		return scriptContent;
	}
	public void setScriptContent(String scriptContent) {
		this.scriptContent = scriptContent;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public long getCreateUser() {
		return createUser;
	}
	public void setCreateUser(long createUser) {
		this.createUser = createUser;
	}
	
	
}

