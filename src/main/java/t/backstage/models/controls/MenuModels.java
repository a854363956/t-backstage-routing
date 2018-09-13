package t.backstage.models.controls;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import t.backstage.error.BusinessException;
import t.backstage.models.entitys.TBaseRole;
import t.backstage.models.entitys.TMenuData;
import t.backstage.routing.annotations.Post;
import t.sql.Session;
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
	 * 根据父节点ID查询当前含有的子节点ID
	 * @param parame menuFather 当前父节点的ID
	 * @return 返回当前所有的子节点信息
	 */
	@Post
	public List<TMenuData> getChildNode(JSONObject parame){
		String menuFather = parame.getString("menuFather");
		Query<TMenuData> query = sessionFactory.getCurrentSession().createQuery("select * from t_menu_data where menuFather=:menuFather order by displayOrder",TMenuData.class);
		query.setParameter("menuFather",menuFather);
		return query.list();
	}
	/**
	 * 获取当前显示排序最大的,也就是优先级最低的显示值
	 * @param parame
	 * @return
	 */
	@Post
	public Double getMaxDisplayOrder(JSONObject parame) {
		String menuFather = parame.getString("menuFather");
		String sql = "select max(displayOrder) as maxDisplayOrder from t_menu_data where menuFather=:menuFather";
		Query<Map<String,Double>> query = sessionFactory.getCurrentSession().createQuery(sql,HashMap.class);
		query.setParameter("menuFather",menuFather);
		return query.uniqueResult().get(t.backstage.models.context.DBMapKey.maxDisplayOrder)+1;
		
	}
	/**
	 * 删除菜单,并删除对应的权限信息
	 * @param j
	 *            -id 要删除的菜单ID
	 *            -menuName 要删除的菜单名称
	 */
	@Post
	public void delMenu(JSONObject j) {
		String id       = j.getString("id");
		String menuName = j.getString("menuName");
		
		if(t.backstage.models.context.StringUtils.isNull(id)) {
			throw new BusinessException(5015,"id");
		}
		
		if(t.backstage.models.context.StringUtils.isNull(menuName)) {
			throw new BusinessException(5015,"menuName");
		}
		
		String sql = "select * from t_menu_data where menuFather=:menuFather";
		Query<TMenuData> tmdQuery = sessionFactory.getCurrentSession().createQuery(sql,TMenuData.class);
		tmdQuery.setParameter("menuFather",id);
		
		
		// 判断是否含有子节点,如果含有子节点则不让删除
		if(tmdQuery.list().size() == 0) {
			Session session = sessionFactory.getCurrentSession();
			session.transactionVoid(()->{
				// 删除对应的权限节点
				sessionFactory.getCurrentSession().nativeDMLSQL("delete from t_menu_authority  where mId = ?",id);
				// 删除路由请求权限
				sessionFactory.getCurrentSession().nativeDMLSQL("delete from t_request_authority where mId = ?",id);
				// 删除菜单节点
				sessionFactory.getCurrentSession().nativeDMLSQL("delete from t_menu_data where id=?",id);
			});
		}else {
			throw new BusinessException(5014,menuName);
		}
	}
	
	/**
	 * 用来更新菜单节点
	 * @param j
	 */
	@Post
	public void updateMenu(JSONObject j) {
		// 菜单名称
		String menuName     = j.getString("menuName");
		// 菜单所属路径
		String menuPath     = j.getString("menuPath");
		// 菜单显示图标
		String menuIcon     = j.getString("menuIcon");
		// 设备类型
		String equipmentId  = j.getString("equipmentId");
		// 显示优先级
		Double displayOrder = j.getDouble("displayOrder");
		// 是否是菜单节点
		Integer isLeaf      = j.getInteger("isLeaf");
		// 父节点ID
		String menuFather   = j.getString("menuFather");
		// 节点ID
		String id           = j.getString("id");
		
		TMenuData tmd = new TMenuData();
		tmd.setId(id);
		tmd.setMenuName(menuName);
		tmd.setMenuPath(menuPath);
		tmd.setMenuIcon(menuIcon);
		tmd.setEquipmentId(equipmentId);
		tmd.setDisplayOrder(displayOrder);
		tmd.setCreateTime(new Date());
		tmd.setCreateUser(t.backstage.models.context.ContextUtils.getCurrentUser().getId());
		tmd.setIsLeaf(isLeaf);
		tmd.setMenuFather(menuFather);
		sessionFactory.getCurrentSession().update(tmd);
	}
	/**
	 * 添加菜单信息,并且对应的添加所属的权限节点
	 * @param json
	 */
	@Post
	public void addMenu(JSONObject j) {
		// 菜单名称
		String menuName     = j.getString("menuName");
		// 菜单所属路径
		String menuPath     = j.getString("menuPath");
		// 菜单显示图标
		String menuIcon     = j.getString("menuIcon");
		// 设备类型
		String equipmentId  = j.getString("equipmentId");
		// 显示优先级
		Double displayOrder = j.getDouble("displayOrder");
		// 是否是菜单节点
		Integer isLeaf      = j.getInteger("isLeaf");
		// 父节点ID
		String menuFather   = j.getString("menuFather");
		if(!"ROOT".equals(menuFather)) {
			Query<TMenuData> qTmd= sessionFactory.getCurrentSession().createQuery("select * from t_menu_data  where id=:id  ",TMenuData.class);
			qTmd.setParameter("id",menuFather);
			TMenuData qTmdUn = qTmd.uniqueResult();
			if(qTmdUn == null) {
				throw new t.backstage.error.BusinessException(5012,menuFather);
			}
			if(qTmdUn.getIsLeaf() != 0) {
				throw new t.backstage.error.BusinessException(5013,menuFather);
			}
		}
	
		// 保存菜单数据
		TMenuData tmd = new TMenuData();
		tmd.setId(t.sql.utils.StringUtils.getUUID());
		tmd.setMenuName(menuName);
		tmd.setMenuPath(menuPath);
		tmd.setMenuIcon(menuIcon);
		tmd.setEquipmentId(equipmentId);
		tmd.setDisplayOrder(displayOrder);
		tmd.setCreateTime(new Date());
		tmd.setAbbreviation(t.backstage.models.context.StringUtils.getPinYinHeadChar(menuName));
		tmd.setCreateUser(t.backstage.models.context.ContextUtils.getCurrentUser().getId());
		tmd.setIsLeaf(isLeaf);
		tmd.setMenuFather(menuFather);
		Session session = sessionFactory.getCurrentSession();
		 session.transactionVoid(()->{
			 session.create(tmd);
			 Query<t.backstage.models.entitys.TBaseRole> rQuery = session.createQuery("select * from t_base_role",t.backstage.models.entitys.TBaseRole.class);
				List<t.backstage.models.entitys.TBaseRole> roles = rQuery.list();
				// 创建的菜单节点
				List<t.backstage.models.entitys.TMenuAuthority > createMenus =new ArrayList<t.backstage.models.entitys.TMenuAuthority >();
				for(t.backstage.models.entitys.TBaseRole tbr : roles) {
					// 初始化所有角色的权限数据
					t.backstage.models.entitys.TMenuAuthority tma = new t.backstage.models.entitys.TMenuAuthority();
					tma.setId(t.sql.utils.StringUtils.getUUID());
					tma.setrId(tbr.getId());
					tma.setmId(tmd.getId());
					tma.setState(0.00);
					tma.setCreateTime(new Date());
					tma.setCreateUser(t.backstage.models.context.ContextUtils.getCurrentUser().getId());
					createMenus.add(tma);
				}
				sessionFactory.getCurrentSession().createBatch(createMenus);
		});
		
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
			 return null;
		 }else {
			 List<Menu> listMenu = new ArrayList<Menu>();
			 for(TMenuData md : tmd ) {
				 Menu menu = new Menu();
				 menu.setChildren(selectByFatherAll(md.getId(),equipmentId));
				 menu.setIcon(md.getMenuIcon());
				 menu.setName(md.getMenuName());
				 menu.setPath(md.getMenuPath());
				 menu.setId(md.getId());
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
				 menu.setId(md.getId());
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
		Query<TMenuData> tmdQuery = sessionFactory.getCurrentSession().createQuery("select * from t_menu_data where menuFather=:menuFather and equipmentId=:equipmentId and isLeaf = 0 order by displayOrder",TMenuData.class);
		tmdQuery.setParameter("menuFather",fatherId);
		tmdQuery.setParameter("equipmentId",equipmentId);
		return tmdQuery.list();
	}
	
	/**
	 * 获取当前所有的角色信息
	 * @param json
	 * @return
	 */
	@Post
	public List<TBaseRole> getTBaseRoleAll(JSONObject json){
		Query<TBaseRole> tbrQuery =  sessionFactory.getCurrentSession().createQuery("select * from t_base_role",TBaseRole.class);
		return tbrQuery.list();
	}
	/**
	 * 设置为可访问权限
	 * @param json
	 */
	@Post
	public void authorizationMenu(JSONObject json) {
		String id = json.getString("id");
		if(t.backstage.models.context.StringUtils.isNull(id)) {
			throw new t.backstage.error.BusinessException(5015,"id");
		}
		sessionFactory.getCurrentSession().nativeDMLSQL("update t_menu_authority set state = 1.0 where id =?",id);
	}
	
	/**
	 * 设置为不可访问权限
	 * @param json
	 */
	@Post
	public void unAuthorizationMenu(JSONObject json) {
		String id = json.getString("id");
		if(t.backstage.models.context.StringUtils.isNull(id)) {
			throw new t.backstage.error.BusinessException(5015,"id");
		}
		sessionFactory.getCurrentSession().nativeDMLSQL("update t_menu_authority set state = 0.0 where id =?",id);
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
	// 唯一ID
	private String     id;
	// 显示名称
	private String     name;
	// 图标
	private String     icon;
	// 路径,用来配置前端页面的
	private String     path;
	// 子节点集
	private List<Menu> children;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
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

