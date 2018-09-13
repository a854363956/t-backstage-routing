package t.backstage.models.controls;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import t.backstage.models.entitys.TDataSource;
import t.backstage.models.entitys.TTableColumn;
import t.backstage.models.entitys.TTableParames;
import t.backstage.routing.annotations.Post;
import t.sql.Session;
import t.sql.query.Query;

/***
 * 用来管理数据表格的通用数据管理
 * @author zhangj
 * @date 2018年8月27日 上午9:17:19
 * @email zhangjin0908@hotmail.com
 */
@org.springframework.stereotype.Service
public class TableSqlModels {
	@org.springframework.beans.factory.annotation.Autowired
	private t.sql.SessionFactory sessionFactory;
	
	/**
	 * 用来处理表格中表单查询的信息
	 * @return    nameCode 代号名称
	 */
	@Post
	public List<TTableParames> formItems(JSONObject j){
		String nameCode = j.getString("nameCode");
		Query<TDataSource>  tdsQuery = sessionFactory.getCurrentSession().createQuery("select * from t_data_source where nameCode = :nameCode",TDataSource.class);
		tdsQuery.setParameter("nameCode",nameCode);
		TDataSource tds = tdsQuery.uniqueResult();
		if(tds  == null) {
			throw new t.backstage.error.BusinessException(5010,nameCode);
		}else {
			Query<TTableParames> ttpQuery = sessionFactory.getCurrentSession().createQuery("select * from t_table_parames where dataSourceId=:dataSourceId",TTableParames.class);
			ttpQuery.setParameter("dataSourceId",tds.getId());
			return ttpQuery.list();
		}
	}
	/**
	 * 查询通用表格的列的信息
	 * @param j   nameCode 代号名称
	 * @return
	 */
	@Post                    
	public List<TTableColumn> column(JSONObject j) {
		String nameCode = j.getString("nameCode");
		Query<TDataSource>  tdsQuery = sessionFactory.getCurrentSession().createQuery("select * from t_data_source where nameCode = :nameCode",TDataSource.class);
		tdsQuery.setParameter("nameCode",nameCode);
		TDataSource tds = tdsQuery.uniqueResult();
		if(tds  == null) {
			throw new t.backstage.error.BusinessException(5010,nameCode);
		}else {
			Query<TTableColumn> tQuery = sessionFactory.getCurrentSession().createQuery("select * from t_table_column  where  dataSourceId =:dataSourceId ",TTableColumn.class);
			tQuery.setParameter("dataSourceId",tds.getId());
			return tQuery.list();
		}
	}
	/**
	 * 通用的查询数据表格信息 目前仅仅只是支持当前的sql数据源
	 * @param j
	 * @return
	 */
	@Post
	public ResultPage select(JSONObject j) {
		JSONObject pagination = j.getJSONObject("pagination");
		// 当前页面大小
		int pageSize       = pagination.getIntValue("pageSize");
		// 当前页面号
		int pageNum        = pagination.getIntValue("pageNum");
		JSONObject condition  =j.getJSONObject("condition");
		// 查询代号
		String nameCode    = condition.getString("nameCode");
		// 当前查询的参数条件
		JSONObject parames = condition.getJSONObject("parames");

		Session session = sessionFactory.getCurrentSession();
		ResultPage rp = new ResultPage();
		// 查询SQL
		String querySql =getQuerySql(nameCode);
		// 汇总SQL
		String countSql =getCountSql(nameCode);
		
		// 设置当前count的数量
		Query<Map<String,Object>> countMapQuery =session.createQuery(countSql,java.util.HashMap.class);
		List<String> regulars =t.backstage.models.context.StringUtils.findRegular(countSql,":[A-Za-z0-9_]+");
		// 以SQL里面的占用符为准,来替换对应的参数
		for(String key : regulars) {
			String _key = key.replace(":","");
			countMapQuery.setParameter(_key,parames.get(_key));
		}
		Map<String,Object> countMap = countMapQuery.uniqueResult();
		rp.setTotal((Long)countMap.get(t.backstage.models.context.DBMapKey.countNum));
		rp.setPageSize(pageSize);
		rp.setPageNum(pageNum);
		
		// 设置当前数据到返回值
		Query<Map<String,Object>> dataMapQuery  =session.createQuery(getPaginationSql(querySql,pageSize,pageNum),java.util.HashMap.class);
		
		List<String> queryRegulars =t.backstage.models.context.StringUtils.findRegular(querySql,":[A-Za-z0-9_]+");
		for(String key : queryRegulars) {
			String _key = key.replace(":","");
			dataMapQuery.setParameter(_key,parames.get(_key));
		}

		rp.setDatas(dataMapQuery.list());
		
		return rp; 
	} 
	/**
	 * 根据sql获取分页的sql查询
	 * @param sql
	 * @return
	 */
	protected String getPaginationSql(String sql,int pageSize ,int pageNum) {
		// 开始页号
		int satrtNum = pageNum *pageSize;
		// 结束页号
		int endNum   = (pageNum+1)*pageSize;
		return sql +" LIMIT "+satrtNum+","+endNum;
	}
	/**
	 * 获取当前数据源
	 * @param nameCode 代码
	 * @return  返回当前的数据源参数
	 */
	public SQLDataSource getDataSource(String nameCode) {
		Query<TDataSource> tQuery = sessionFactory.getCurrentSession().createQuery("select * from t_data_source where nameCode=:nameCode",TDataSource.class);
		tQuery.setParameter("nameCode",nameCode);
		TDataSource tbs = tQuery.uniqueResult();
		if(tbs == null ) {
			// 当前代号 nameCode 未维护,请检查是否维护此代号
			throw new t.backstage.error.BusinessException(5008,nameCode);
		}else {
			try {
				JSONObject j = com.alibaba.fastjson.JSON.parseObject(tbs.getScriptContent());
				String querySql = new String(java.util.Base64.getDecoder().decode(j.getString("querySql")));
				String countSql = new String (java.util.Base64.getDecoder().decode(j.getString("countSql")));
				SQLDataSource sqlDs = new SQLDataSource();
				sqlDs.setCountSql(countSql);
				sqlDs.setQuerySql(querySql);
				return sqlDs;
			} catch (Exception e) {
				// 当前维护的数据源格式不正确,请检查异常信息
				throw new t.backstage.error.BusinessException(5009,nameCode,e.getMessage());
			}
		}
	}
	/**
	 * 获取汇总SQL
	 * @param nameCode sql代号
	 * @return 返回汇总的sql
	 */
	public String getCountSql(String nameCode) {
		return getDataSource(nameCode).getCountSql();
	}
	
	/**
	 * 获取查询的SQL
	 * @param nameCode sql代号
	 * @return 返回查询的sql
	 */
	public String getQuerySql(String nameCode) {
		return getDataSource(nameCode).getQuerySql();
	}
}
/**
 * 当前表格查询的分页对象
 * @author zhangj
 */
class ResultPage{
	// 当前分页第几页
	private int pageNum; 
	// 当前分页的大小
	private int pageSize;
	// 当前查询到的数据
	@SuppressWarnings("rawtypes")
	private List datas;
	// 当前查询数据的条目数
	private long total;
	
	
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public int getPageNum() {
		return pageNum;
	}
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	@SuppressWarnings("unchecked")
	public List<Object> getDatas() {
		return datas;
	}
	public void setDatas(@SuppressWarnings("rawtypes") List datas) {
		this.datas = datas;
	}
	
}
/**
 * 当前查询表格的数据源
 * @author zhangj
 */
class SQLDataSource{
	// 当前查询的语句
	private String querySql;
	// 当前汇总的语句
	private String countSql;
	public String getQuerySql() {
		return querySql;
	}
	public void setQuerySql(String querySql) {
		this.querySql = querySql;
	}
	public String getCountSql() {
		return countSql;
	}
	public void setCountSql(String countSql) {
		this.countSql = countSql;
	}
	
}

