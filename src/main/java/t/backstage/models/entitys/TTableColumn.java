package t.backstage.models.entitys;

import javax.persistence.Table;

import t.sql.interfaces.DTO;

/***
 * 表格列的定义
 * @author zhangj
 * @date 2018年8月28日 下午12:03:42
 * @email zhangjin0908@hotmail.com
 */
@Table(name = "t_table_column")
public class TTableColumn implements DTO{
	private static final long serialVersionUID = 2398060085589304609L;
	// 唯一ID
	@javax.persistence.Id
	@javax.persistence.Column
	private String id ;
	// 数据源Id
	@javax.persistence.Column
	private String dataSourceId;
	// 显示的列的名称
	@javax.persistence.Column
	private String headerName;
	// 字段名称
	@javax.persistence.Column
	private String field;
	// 宽度
	@javax.persistence.Column
	private int    width;
	// 是否隐藏
	@javax.persistence.Column
	private int    hide;
	// 是否可编辑
	@javax.persistence.Column
	private int    editable;
	// 优先级
	@javax.persistence.Column
	private double displayOrder;
	// 表格类型
	@javax.persistence.Column
	private String type;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDataSourceId() {
		return dataSourceId;
	}
	public void setDataSourceId(String dataSourceId) {
		this.dataSourceId = dataSourceId;
	}
	public String getHeaderName() {
		return headerName;
	}
	public void setHeaderName(String headerName) {
		this.headerName = headerName;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHide() {
		return hide;
	}
	public void setHide(int hide) {
		this.hide = hide;
	}
	public int getEditable() {
		return editable;
	}
	public void setEditable(int editable) {
		this.editable = editable;
	}
	
}

