package t.backstage.models.entitys;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;

import t.sql.interfaces.DTO;

/***
 * 多语言支持的资料维护表
 * @author zhangj
 * @date 2018年8月20日 下午4:10:10
 * @email zhangjin0908@hotmail.com
 */
@Table(name = "t_base_language")
public class TBaseLanguage implements DTO {
	private static final long serialVersionUID = 4252652665944274208L;
	// 用户唯一ID
	@Id
	@javax.persistence.Column
	private String id;
	// 语言类型 0表示基础语言
	@javax.persistence.Column
	private int    type;
	// 语言代号
	@javax.persistence.Column
	private int    code;
	// 显示名称
	@javax.persistence.Column
	private String label;
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
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
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

