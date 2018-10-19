package t.backstage.models.controls;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONObject;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
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
	 * 将当前的sql转换为查询不需要条件的sql，去掉where条件查询的sql
	 * @param sql
	 * @return
	 * @throws JSQLParserException 
	 */
	private String toNotWhere(String sql) throws JSQLParserException {
		String realSql = sql.toString();
		// 去掉SQL中的注释部分
		realSql=delSqlComment(realSql);
		List<String> parames = t.backstage.models.context.StringUtils.findRegular(realSql,":[A-Za-z_]+");
		for(String param :parames) {
			// 将SQL中的 select * from dual where name = :name 替换为 name = 'name' 避免解析器出现BUG
			realSql=realSql.replaceAll(param,String.format("'%s'",param));
		}
		
		Select select = (Select) CCJSqlParserUtil.parse(realSql);
		PlainSelect ps = (PlainSelect) select.getSelectBody();
		Expression where = ps.getWhere();
		if(where == null) {
			return realSql;
		}else {
			String swhere = where.toString();
			if(t.backstage.models.context.StringUtils.isNull(swhere)) {
				return realSql;
			}else {
				return realSql.replace(swhere, " 1=2 ");
			}
		}
	}
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
	 * 更新当前列的信息
	 */
	@Post
	public void updateColumn(JSONObject json) {
		TTableColumn ttc = com.alibaba.fastjson.JSON.parseObject(json.toJSONString(),new com.alibaba.fastjson.TypeReference<TTableColumn>() {});
		if(ttc.getDataSourceId() == null) {
			throw new t.backstage.error.BusinessException(5015,"dataSourceId");
		}
		sessionFactory.getCurrentSession().update(ttc);
	}

	/**
	 * 用来创建表格列的信息   
	 * @param json
	 *            -querySql 查询的脚本信息
	 *            -id       当前表格的唯一ID
	 * @throws SQLException 
	 * @throws JSQLParserException 
	 */
	@Post
	public void generateColumn(JSONObject json ) throws SQLException, JSQLParserException{
		// 查询SQL脚本信息
		String querySql = json.getString("querySql");
		// 唯一id
		Long id       = json.getLong("id");
		
		if(t.backstage.models.context.StringUtils.isNull(querySql)) {
			throw new t.backstage.error.BusinessException(5015,"querySql");
		}
		if(id == null) {
			throw new t.backstage.error.BusinessException(5015,"id");
		}
		
		List<TTableColumn> ttcs = new ArrayList<TTableColumn>();
		
		// JDBC Connection 连接
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = sessionFactory.openConnection();
			ps =connection.prepareStatement(toNotWhere(querySql));
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData rsm = rs.getMetaData();
			int count = rsm.getColumnCount();
			
			// 要查询的所有字段
			List<String> fields = new ArrayList<String>();
			
			// 获取到当前SQL列的信息
			for (int i = 0; i < count; i++) {
				String label = rsm.getColumnLabel(i+1);
				TTableColumn ttc = new TTableColumn();
				ttc.setField(label);
				ttc.setHeaderName(label);
				ttc.setDataSourceId(id);
				ttc.setEditable(1);
				ttc.setHide(1);
				ttc.setWidth(120);
				ttc.setType("text");
				ttc.setId(t.backstage.models.context.ContextUtils.getUUID());
				fields.add(label);
				ttcs.add(ttc);
			}
			
			Query<TTableColumn> ttcQuery = sessionFactory.getCurrentSession().createQuery("select * from t_table_column where field in :field",TTableColumn.class);
			ttcQuery.setParameter("field",fields);
			List<TTableColumn> ttcList = ttcQuery.list();
			for(TTableColumn ttc : ttcs) {
				List<TTableColumn> haveTTC = ttcList.stream().filter((t)->{
					if(t.getField().equals(ttc.getField())) {
						return true;
					}else {
						return false;
					}
				}).collect(java.util.stream.Collectors.toList());
				// 如果为空跳出当前循环默认当前设置
				if(haveTTC == null || haveTTC.size() == 0 ) {
					continue;
				}else {
					// 如果里面有符合条件的数据，那么就将值设置为默认的数据值
					TTableColumn httc = null;
					
					// 如果存在生成了数据，那么就取生成过后的数据，如果未生成数据，那么就拦截数据
					List<TTableColumn> lttc = haveTTC.stream().filter((d)->{
						if(d.getDataSourceId() == id) {
							return true;
						}else {
							return false;
						}
					}).collect(java.util.stream.Collectors.toList());
					if(lttc == null || lttc.size() == 0) {
					  httc = haveTTC.get(0);
					}else {
					  httc = lttc.get(0);
					}
					
					ttc.setEditable(httc.getEditable());
					ttc.setHeaderName(httc.getHeaderName());
					ttc.setHide(httc.getHide());
					ttc.setType(httc.getType());
					ttc.setWidth(httc.getWidth());
					continue;
				}
			}
			// 将数据存储到数据库中
			Session session = sessionFactory.getCurrentSession();
			session.transactionVoid(()->{
				// 优先删除生成的数据
				session.nativeDMLSQL("delete from t_table_column where dataSourceId = ?",id);
				// 然后重新生成节点数据
				session.createBatch(ttcs);
			});
			
		}finally {
			if(ps != null) {
				ps.close();
			}
			if(connection != null) {
				connection.close();
			}
		}
	}
	/**
	 * 添加数据源
	 * @param json
	 *           -nameCode 用户代号
	 *           -dataType 数据类型 默认为0表示使用sql处理数据源
	 *           -remarks  当前数据源的备注
	 */
	@Post
	public void addSqlSource(JSONObject json) {
		// 用户代号
		String nameCode = json.getString("nameCode");
		// 数据类型
		Integer dataType = json.getInteger("dataType");
		// 备注
		String remarks  = json.getString("remarks");
		
		// 校验是否输入进来的参数是否为空
		if(t.backstage.models.context.StringUtils.isNull(nameCode)) {
			throw new t.backstage.error.BusinessException(5015,"nameCode");
		}
		if(dataType == null) {
			throw new t.backstage.error.BusinessException(5015,"dataType");
		}
		if(t.backstage.models.context.StringUtils.isNull(remarks)) {
			throw new t.backstage.error.BusinessException(5015,"remarks");
		}
	
		Query<TDataSource> tdsQuery = sessionFactory.getCurrentSession().createQuery("select * from t_data_source where nameCode=:nameCode ",TDataSource.class);
		tdsQuery.setParameter("nameCode",nameCode);
		// 如果nameCode存在，那么就不让他继续添加，不存在才添加
		if(tdsQuery.list().size() == 0) {
			TDataSource tds = new TDataSource();
			tds.setNameCode(nameCode);
			tds.setDataType(dataType);
			tds.setRemarks(remarks);
			tds.setCreateTime(new Date());
			tds.setCreateUser(t.backstage.models.context.ContextUtils.getCurrentUser().getId());
			JSONObject scriptContent = new JSONObject();
			scriptContent.put("querySql",new String(java.util.Base64.getEncoder().encode("-- 常用变量:_USER 当前系统登入的人员ID,:_WAREHOUSE 当前登入人员所属的仓库ID\n\n".getBytes())));
			scriptContent.put("countSql",new String(java.util.Base64.getEncoder().encode("-- count(1) as countNum 需要在这样进行别名的修改 \n\n".getBytes())));
			tds.setScriptContent(com.alibaba.fastjson.JSON.toJSONString(scriptContent));
			tds.setId(t.backstage.models.context.ContextUtils.getUUID());
			
			sessionFactory.getCurrentSession().create(tds);
		}else {
			throw new t.backstage.error.BusinessException(5018,nameCode);
		}
		

	}
	/**
	 * 根据当前的ID删除符合当前id的数据
	 * @param json
	 *             -id 用户唯一id
	 */
	@Post
	public void delSqlSource(JSONObject json) {
		// 获取当前要删除数据的ID
		Long id = json.getLong("id");
		if(id == null) {
			throw new t.backstage.error.BusinessException(5015,"id");
		}
		Session session = sessionFactory.getCurrentSession();
		session.transactionVoid(()->{
			// 先删除相关的明细
			session.nativeDMLSQL("delete from t_table_column where dataSourceId = ?",id);
			// 在删除当前的汇总
			session.nativeDMLSQL("delete from t_data_source  where id = ?",id);
		});
	}
	/**
	 * 根据数据信息到当前数据源里面
	 * @param json
	 *       -querySql 查询的SQL
	 *       -countSql 查询的count的sql
	 *       -id       已存在的唯一ID
	 *       -remarks  备注信息
	 */
	@Post
	public void updateSql(JSONObject json) {
		// 查询的SQL
		String querySql = json.getString("querySql");
		// 查询的count的sql
		String countSql = json.getString("countSql");
		// 当前查询的数据源id
		String id       = json.getString("id");
		// 当前数据源的备注
		String remarks  = json.getString("remarks");
		if(t.backstage.models.context.StringUtils.isNull(querySql)) {
			throw new t.backstage.error.BusinessException(5015,"querySql");
		}
		if(t.backstage.models.context.StringUtils.isNull(countSql)) {
			throw new t.backstage.error.BusinessException(5015,"countSql");
		}
		if(t.backstage.models.context.StringUtils.isNull(id)) {
			throw new t.backstage.error.BusinessException(5015,"id");
		}
		Query<TDataSource> tdsQuery = sessionFactory.getCurrentSession().createQuery("select * from t_data_source where id=:id",TDataSource.class);
		tdsQuery.setParameter("id",id);
		TDataSource tds = tdsQuery.uniqueResult();
		if(tds == null) {
			throw new t.backstage.error.BusinessException(5017,id);
		}else {
			// 更新数据到数据源
			JSONObject scriptContent = new JSONObject();
			scriptContent.put("querySql",java.util.Base64.getEncoder().encodeToString(querySql.getBytes()));
			scriptContent.put("countSql",java.util.Base64.getEncoder().encodeToString(countSql.getBytes()));
			tds.setScriptContent(com.alibaba.fastjson.JSON.toJSONString(scriptContent));
			tds.setRemarks(remarks);
			sessionFactory.getCurrentSession().update(tds);
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
		Query<TDataSource>  tdsQuery = sessionFactory.getCurrentSession().createQuery("select * from t_data_source where nameCode = :nameCode ",TDataSource.class);
		tdsQuery.setParameter("nameCode",nameCode);
		TDataSource tds = tdsQuery.uniqueResult();
		if(tds  == null) {
			throw new t.backstage.error.BusinessException(5010,nameCode);
		}else {
			Query<TTableColumn> tQuery = sessionFactory.getCurrentSession().createQuery("select * from t_table_column  where  dataSourceId =:dataSourceId order by displayOrder  ",TTableColumn.class);
			tQuery.setParameter("dataSourceId",tds.getId());
			return tQuery.list();
		}
	}
	
	/**
	 * 删除SQL注释
	 **/
	private String delSqlComment(String sql) {
		Pattern p = Pattern.compile("(?ms)('(?:''|[^'])*')|--.*?$|/\\*.*?\\*/");  
		return p.matcher(sql).replaceAll("$1");  
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
		String querySql =delSqlComment(getQuerySql(nameCode));
		// 汇总SQL
		String countSql =delSqlComment(getCountSql(nameCode));
		
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
			if(key.equals(":_USER")) { // 常用变量人员ID
				dataMapQuery.setParameter("_USER",t.backstage.models.context.ContextUtils.getCurrentUser().getId());
			}else if(key.equals(":_WAREHOUSE")) { // 常用变量仓库ID
				dataMapQuery.setParameter("_WAREHOUSE",t.backstage.models.context.ContextUtils.getCurrentUser().getWhId());
			}else {
				String _key = key.replace(":","");
				dataMapQuery.setParameter(_key,parames.get(_key));
			}
		}

		rp.setDatas(dataMapQuery.list());
		
		return rp; 
	} 
	/**
	 * 根据sql获取分页的sql查询,此方法根据不同的数据可以进行重载操作
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

