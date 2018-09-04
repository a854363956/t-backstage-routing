package t.backstage.models.context;

/***
 * 所有采用Map进行查询的都需要在此处进行维护Key方便以后做数据库迁移
 * @author zhangj
 * @date 2018年8月23日 下午2:16:54
 * @email zhangjin0908@hotmail.com
 */
public class DBMapKey {
	// 查询消息的count条数
	public static final String notifyCount="notifyCount";
	// 获取菜单最大的显示值,对于前台而已就是排序最低的显示值
	public static final String maxDisplayOrder="maxDisplayOrder";
	// 获取通用SQL的count字段
	public static final String countNum="countNum";
}

