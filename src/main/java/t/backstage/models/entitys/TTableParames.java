package t.backstage.models.entitys;

import javax.persistence.Table;

import t.sql.interfaces.DTO;

/***
 * 表格参数的实体对象 
 * @author zhangj
 * @date 2018年8月29日 上午9:56:20
 * @email zhangjin0908@hotmail.com
 */
@Table(name ="t_table_parames")
public class TTableParames implements DTO{
	private static final long serialVersionUID = 1736089542299033814L;
	@javax.persistence.Column
	private String id;
	@javax.persistence.Column
	private int    colon;
	@javax.persistence.Column
	private String label;
	@javax.persistence.Column
	private int    required;
	@javax.persistence.Column
	private String name;
	@javax.persistence.Column
	private String dataSourceId;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getColon() {
		return colon;
	}
	public void setColon(int colon) {
		this.colon = colon;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public int getRequired() {
		return required;
	}
	public void setRequired(int required) {
		this.required = required;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDataSourceId() {
		return dataSourceId;
	}
	public void setDataSourceId(String dataSourceId) {
		this.dataSourceId = dataSourceId;
	}
}

