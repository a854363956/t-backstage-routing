package t.backstage.models.entitys;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import t.sql.interfaces.DTO;

/***
 * 用来管理菜单的基础实体
 * 
 * @author zhangj
 * @date 2018年8月22日 下午1:56:49
 * @email zhangjin0908@hotmail.com
 */
@Table(name = "t_menu_data")
public class TMenuData implements DTO {
	private static final long serialVersionUID = 544713534052516992L;
	// 唯一ID
	@Id
	@Column
	private String id;
	// 菜单名称
	@Column
	private String menuName;
	// 菜单图标
	@Column
	private String menuIcon;
	// 菜单路径
	@Column
	private String menuPath;
	// 菜单父节点
	@Column
	private String menuFather;
	// 创建人
	@Column
	private String createUser;
	// 创建时间
	@Column
	private Date createTime;
	// 是否含有子节点
	@Column
	private Integer isLeaf;
	// 所属设备
	@Column
	private String equipmentId;
	// 排序字段
	@Column
	private Double displayOrder;
	// 助记码
	@Column
	private String abbreviation;
	
	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public String getMenuIcon() {
		return menuIcon;
	}

	public void setMenuIcon(String menuIcon) {
		this.menuIcon = menuIcon;
	}

	public String getMenuPath() {
		return menuPath;
	}

	public void setMenuPath(String menuPath) {
		this.menuPath = menuPath;
	}

	public String getMenuFather() {
		return menuFather;
	}

	public void setMenuFather(String menuFather) {
		this.menuFather = menuFather;
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

	public Integer getIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(Integer isLeaf) {
		this.isLeaf = isLeaf;
	}

	public String getEquipmentId() {
		return equipmentId;
	}

	public void setEquipmentId(String equipmentId) {
		this.equipmentId = equipmentId;
	}

	public Double getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Double displayOrder) {
		this.displayOrder = displayOrder;
	}
}
