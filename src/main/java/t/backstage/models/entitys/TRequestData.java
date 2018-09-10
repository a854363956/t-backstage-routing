package t.backstage.models.entitys;

import java.util.Date;

import javax.persistence.Table;


import t.sql.interfaces.DTO;

/***
 * 路由信息维护
 * @author zhangj
 * @date 2018年9月4日 上午10:30:57
 * @email zhangjin0908@hotmail.com
 */
@Table(name="t_request_data")
public class TRequestData implements DTO {
	private static final long serialVersionUID = -6999051121559693801L;
	// 唯一ID
	@javax.persistence.Column
	private String id;
	// 请求的URL地址
	@javax.persistence.Column
	private String url;
	// 创建人
	@javax.persistence.Column
	private String createUser;
	// 创建时间
	@javax.persistence.Column
	private Date   createTime;
	// 备注
	@javax.persistence.Column
	private String remarks;
	// 是否允许在未登入的情况下访问 0 表示不允许在未登入的情况下访问 1表示允许访问
	@javax.persistence.Column
	private int loginAuthorize;
	
	
	public int getLoginAuthorize() {
		return loginAuthorize;
	}
	public void setLoginAuthorize(int loginAuthorize) {
		this.loginAuthorize = loginAuthorize;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
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
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
}

