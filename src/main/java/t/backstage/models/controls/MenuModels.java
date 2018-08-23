package t.backstage.models.controls;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

import t.backstage.models.entitys.TMenuData;
import t.backstage.routing.annotations.Post;
import t.sql.query.Query;

/***
 * 用来处理系统菜单
 * @author zhangj
 * @date 2018年8月22日 下午1:22:30
 * @email zhangjin0908@hotmail.com
 */
@org.springframework.stereotype.Service
public class MenuModels {
	@org.springframework.beans.factory.annotation.Autowired
	private t.sql.SessionFactory sessionFactory;
	
	/**
	 * 获取当前所有的菜单信息,此菜单带当前登入的权限
	 * @param parame  无任何参数
	 * @return
	 */
	@Post
	public List<Menu> getAccessibleMenus(JSONObject parame) {
		return selectByPermissionFatherAll("ROOT",t.backstage.models.context.ContextUtils.getCurrentSession().getEquipmentId());
	}
	
	/**
	 * 获取当前所有的菜单信息,此菜单信息不带任何相关的权限信息
	 * @param parame  无任何参数
	 * @return
	 */
	@Post
	public List<Menu> getAllMenus(JSONObject parame) {
		return selectByFatherAll("ROOT",t.backstage.models.context.ContextUtils.getCurrentSession().getEquipmentId());
	}
	
	/**
	 * 根据父节点ID,以及设备类型递归出当前所有的节点信息
	 * @param fatherId        父节点ID
	 * @param equipmentId     当前设备类型
	 * @return
	 */
	public List<Menu> selectByFatherAll(String fatherId,String equipmentId) {
		 List<TMenuData> tmd = selectByFather(fatherId, equipmentId);
		 if(tmd.size() == 0) {
			 return new ArrayList<Menu>();
		 }else {
			 List<Menu> listMenu = new ArrayList<Menu>();
			 for(TMenuData md : tmd ) {
				 Menu menu = new Menu();
				 menu.setChildren(selectByFatherAll(md.getMenuFather(),equipmentId));
				 menu.setIcon(md.getMenuIcon());
				 menu.setName(md.getMenuName());
				 menu.setPath(md.getMenuPath());
				 listMenu.add(menu);
			 }
			 return listMenu;
		 }
	}
	/**
	 * 根据父节点ID,以及设备类型递归出当前所有带权限的节点信息
	 * @param fatherId        父节点ID
	 * @param equipmentId     当前设备类型
	 * @return
	 */
	public List<Menu> selectByPermissionFatherAll(String fatherId,String equipmentId) {
		 List<TMenuData> tmd = selectByPermissionFather(fatherId, equipmentId);
		 if(tmd.size() == 0) {
			 return null;
		 }else {
			 List<Menu> listMenu = new ArrayList<Menu>();
			 for(TMenuData md : tmd ) {
				 Menu menu = new Menu();
				 menu.setChildren(selectByPermissionFatherAll(md.getId(),equipmentId));
				 menu.setIcon(md.getMenuIcon());
				 menu.setName(md.getMenuName());
				 menu.setPath(md.getMenuPath());
				 listMenu.add(menu);
			 }
			 return listMenu;
		 }
	}
	
	/**
	 * 根据当前的菜单节点查询当前下所有的菜单节点
	 * @param fatherId  当前菜单节点的节点ID
	 * @return  返回查询到的所有菜单节点
	 */
	public List<TMenuData> selectByFather(String fatherId,String equipmentId){
		Query<TMenuData> tmdQuery = sessionFactory.getCurrentSession().createQuery("select * from t_menu_data where menuFather=:menuFather and equipmentId=:equipmentId order by displayOrder",TMenuData.class);
		tmdQuery.setParameter("menuFather",fatherId);
		tmdQuery.setParameter("equipmentId",equipmentId);
		return tmdQuery.list();
	}
	
	/**
	 * 根据当前的菜单节点查询当前下所有带权限的菜单节点
	 * @param fatherId  当前菜单节点的节点ID
	 * @return  返回查询到的所有带权限的菜单节点
	 */
	public List<TMenuData> selectByPermissionFather(String fatherId,String equipmentId){
		String sql = "select \n" + 
				"	md.id,\n" + 
				"	md.menuPath,\n" + 
				"	md.menuName,\n" + 
				"	md.menuIcon,\n" + 
				"	md.menuFather,\n" + 
				"	md.isLeaf,\n" + 
				"	md.equipmentId,\n" + 
				"	md.createUser,\n" + 
				"	md.createTime,\n" + 
				"	ma.state\n" + 
				"from t_menu_data md left join t_menu_authority ma \n" + 
				"on  md.id = ma.mId  \n" + 
				"where ma.state = 1.0  and  md.equipmentId =:equipmentId and md.menuFather =:menuFather";
		Query<TMenuData> tmdQuery = sessionFactory.getCurrentSession().createQuery(sql,TMenuData.class);
		tmdQuery.setParameter("menuFather",fatherId);
		tmdQuery.setParameter("equipmentId",equipmentId);
		return tmdQuery.list();
	}
}
/**
 * 菜单的基础实体类,主要用于返回给前端界面的
 * @author zhangj
 * @date 2018年8月22日 下午1:22:30
 * @email zhangjin0908@hotmail.com
 */
class Menu{
	// 显示名称
	private String     name;
	// 图标
	private String     icon;
	// 路径,用来配置前端页面的
	private String     path;
	// 子节点集
	private List<Menu> children;
	

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public List<Menu> getChildren() {
		return children;
	}
	public void setChildren(List<Menu> children) {
		this.children = children;
	}
}

