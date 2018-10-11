package t.backstage.models.controls;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import t.backstage.error.BusinessException;
import t.backstage.models.entitys.TBaseRole;
import t.backstage.models.entitys.TBaseSession;
import t.backstage.models.entitys.TBaseUser;
import t.backstage.models.entitys.TBaseWarehouse;
import t.backstage.routing.annotations.Post;
import t.sql.query.Query;

/***
 * 用来处理用户登入的model模块
 * @author zhangj
 * @date 2018年8月20日 下午3:25:27
 * @email zhangjin0908@hotmail.com
 */
@org.springframework.stereotype.Service
public class UserModels {
	@org.springframework.beans.factory.annotation.Autowired
	private t.sql.SessionFactory sessionFactory;
	@org.springframework.beans.factory.annotation.Autowired
	private NotifyModels notifyModels;
	
	/***
	 * 根据人员id查询对应的人员名称
	 * @return
	 */
	@Post
	public String getUserNameByUserId(JSONObject j) {
		Long id =j.getLong("id");
		if(id == null) {
			throw new BusinessException(5015,"id");
		}
		String sql ="select * from t_base_user where id =:id" ;
		Query<TBaseUser> userQuery = sessionFactory.getCurrentSession().createQuery(sql,TBaseUser.class);
		userQuery.setParameter("id",id);
		
		TBaseUser tbUser= userQuery.uniqueResult();
		if(tbUser == null ) {
			throw new BusinessException(5000);
		}else {
			return tbUser.getUserName();
		}
	}
	/**
	 * 根据角色id查询对应的角色名称
	 * @return
	 */
	@Post
	public String getRoleNameByRoleId(JSONObject j) {
		Long id =j.getLong("id");
		if(id == null) {
			throw new BusinessException(5015,"id");
		}
		String sql ="select * from t_base_role  where id =:id" ;
		Query<TBaseRole> userQuery = sessionFactory.getCurrentSession().createQuery(sql,TBaseRole.class);
		userQuery.setParameter("id",id);
		
		TBaseRole tbRole= userQuery.uniqueResult();
		if(tbRole == null ) {
			throw new BusinessException(5019);
		}else {
			return tbRole.getRoleName();
		}
	}
	/**
	 * 根据仓库id查询对应的仓库名称
	 * @return
	 */
	@Post
	public String getWarehouseNameByWarehouseId(JSONObject j) {
		Long id =j.getLong("id");
		if(id == null) {
			throw new BusinessException(5015,"id");
		}
		String sql ="select * from t_base_warehouse  where id =:id" ;
		Query<TBaseWarehouse> userQuery = sessionFactory.getCurrentSession().createQuery(sql,TBaseWarehouse.class);
		userQuery.setParameter("id",id);
		
		TBaseWarehouse tb= userQuery.uniqueResult();
		if(tb == null ) {
			throw new BusinessException(5019);
		}else {
			return tb.getName();
		}	
	}
	
	/**
	 * 获取当前用户的用户信息
	 * @param j
	 * @return   
	 */
	@Post
	public Map<String,Object> userInfo(JSONObject j) {
		TBaseUser tbu = t.backstage.models.context.ContextUtils.getCurrentUser();
		Map<String,Object> result = new java.util.HashMap<String,Object>();
		// 用户名称
		result.put("name",tbu.getUserName());
		// 用户头像
		result.put("avatar",tbu.getAvatar());
		// 用户id
		result.put("userid",tbu.getId());
		// 用户未提醒的消息
		result.put("notifyCount",notifyModels.notifyCount());
		return result;
	}
	/**
	 * 根据Id删除当前用户
	 * @param j
	 */
	@Post
	public void delUser(JSONObject j ) {
		String id = j.getString("id");
		if(t.backstage.models.context.StringUtils.isNull(id)) {
			throw new BusinessException(5015,"id");
		}
		sessionFactory.getCurrentSession().nativeDMLSQL("delete from t_base_user where id =?",id);
	}
	
	/**
	 * 用户登入 PS: 只支持一个仓库一个用户,不支持一个用户对应多个仓库
	 * 调用的地址  userModels/Login
	 * @param j
	 *            -userName     用户名称
	 *            -password     用户密码
	 *            -loginMode    用户登入的模式是网页登入,还是其他方式登入 0表示网页登入
	 *            -whId         当前用户所属的仓库
	 * @return    如果用户登入成功那么将返回一个当前的sessionId给用户
	 * @throws NoSuchAlgorithmException
	 *           返回错误代号 
	 *              -5000  
	 *              -5001
	 *              -5002
	 *              -5007
	 */
	@Post
	public  String login(JSONObject j) throws NoSuchAlgorithmException {
		// 用户名称
		String userName =j.getString("userName");
		// 用户密码
		String password =j.getString("password");
		// 用户登入的模式是网页登入,还是其他方式登入
		double loginMode=j.getDoubleValue("loginMode");
		// 用户登入的设备编号 0 表示采用网页的方式进登入
		long equipmentId=j.getLongValue("equipmentId");
		Query<TBaseUser> query =sessionFactory.getCurrentSession().createQuery("select * from  t_base_user where loginName =:loginName",t.backstage.models.entitys.TBaseUser.class);
		query.setParameter("loginName",userName);
		List<TBaseUser> result = query.list();
		String ipAddr = t.backstage.models.context.ContextUtils.getIpAddr();
		// 如果admin登入的是非127.0.0.1的地址访问，那么将禁止此用户登入
		if("admin".equals(userName) &&  "127.0.0.1".equals(ipAddr)) {
			if(result.isEmpty()) {
				// 5000 表示根据用户名称和仓库Id未找到对应的用户资料
				throw new t.backstage.error.BusinessException(5000);
			}else {
				String pas = t.backstage.models.context.ContextUtils.md5(password);
				// 判断输入的密码等于查询出来的密码,并且输入的仓库ID等于当前输入的仓库ID
				if(result.get(0).getPassword().equals(pas) ) {
					Query<TBaseRole> trQuery = sessionFactory.getCurrentSession().createQuery("select * from t_base_role where id = :id ",t.backstage.models.entitys.TBaseRole.class);
					
					trQuery.setParameter("id",result.get(0).getrId());
					if(trQuery.list().size() <= 0) {
						// 表示当前登入人的帐号密码都正确,但是没有维护相关的角色信息
						throw new t.backstage.error.BusinessException(5002);
					}else {
						Query<TBaseSession> tbSession = sessionFactory.getCurrentSession().createQuery("select * from t_base_session where uId=:uId and loginMode =:loginMode",TBaseSession.class);
						tbSession.setParameter("uId",result.get(0).getId());
						tbSession.setParameter("loginMode",loginMode);
						List<TBaseSession> tbList = tbSession.list();
						// 如果用户没有退出,那么直接更新他的创建时间
						if(tbList.size() != 0) {
							TBaseSession tb = tbList.get(0);
							tb.setCreateTime(new Date());
							sessionFactory.getCurrentSession().update(tb);
							return tb.getId();
						}else {
							// 如果用户是第一次登入,那么生成t_base_session的数据
							TBaseSession tbs = new TBaseSession();
							String id = t.backstage.models.context.StringUtils.getUUID();
							tbs.setId(id);
							tbs.setIpAddr(t.backstage.models.context.ContextUtils.getIpAddr());
							tbs.setLoginMode(loginMode);
							tbs.setuId(result.get(0).getId());
							tbs.setWhId(result.get(0).getWhId());
							tbs.setCreateTime(new Date());
							tbs.setEquipmentId(equipmentId);
							sessionFactory.getCurrentSession().create(tbs);
							return id;
						}
					}
				}else {
					// 5001 用户帐号正确,但是用户的密码不正确。
					throw new t.backstage.error.BusinessException(5001);
				}
			}
		}else {
			// 5007 admin用户只能在本地登入,无法在除127.0.0.1的其他地址内登入
			throw new t.backstage.error.BusinessException(5007);
		}
		
	}
	
	/**
	 * 用户登出,当用户点击退出按钮,后台清空t_base_session表,并且前台退出到页面,让用户重新登入
	 */
	@Post
	public void signOut(JSONObject j) {
		sessionFactory.getCurrentSession().delete(t.backstage.models.context.ContextUtils.getCurrentSession());
	}
	
	/**
	 * 用户更新密码,需当前密码,以及两次新的密码
	 * @param j
	 * @throws NoSuchAlgorithmException
	 */
	@Post
	public void updatePassword(JSONObject j) throws NoSuchAlgorithmException {
		// 原始密码
		String password           = j.getString("password");
		// 新密码
		String newPassword        = j.getString("newPassword");
		// 再次确认的密码
		String newPasswordConfirm = j.getString("newPasswordConfirm");
		
		if(newPassword == null || "".equals(newPassword)) {
			throw new t.backstage.error.BusinessException(5003);
		}
		if(newPasswordConfirm == null || "".equals(newPassword)) {
			throw new t.backstage.error.BusinessException(5004);
		} 
		if(newPasswordConfirm.equals(newPassword)) {
			TBaseUser pass = t.backstage.models.context.ContextUtils.getCurrentUser();
			String newPass = t.backstage.models.context.ContextUtils.md5(password);
			if(newPass.equals(pass.getPassword())) {
				pass.setPassword(t.backstage.models.context.ContextUtils.md5(newPassword));
				sessionFactory.getCurrentSession().update(pass);
				return;
			}else {
				throw new t.backstage.error.BusinessException(5006);
			}
			
		}else {
			throw new t.backstage.error.BusinessException(5005);
		}
	}
}

