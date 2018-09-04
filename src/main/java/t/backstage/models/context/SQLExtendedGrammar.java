package t.backstage.models.context;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

/***
 * 为当前的SQL添加扩展的语法
 * @author zhangj
 * @date 2018年8月29日 下午5:23:04
 * @email zhangjin0908@hotmail.com
 */
public class SQLExtendedGrammar {
	/**
	 * 如果SQL中含有\/*NOTNULL->`字段名称`->{`要执行的sql`}*\/ {}括号中表示如果参数不为空的清空下会添加此数据
	 * @param sql
	 * @param j
	 * @return
	 */
	public static String notNull(String sql,JSONObject j) {
		String regular="/\\*\\s*NOTNULL\\s*->\\s*[A-Za-z0-9_]+\\s*->\\s*\\{.*?\\}\\s*\\*/";
		List<String> notNullStr = StringUtils.findRegular(sql,regular);
		for(String str : notNullStr) {
			String addSqlFieldRegular = "\\{\\s*.*?\\}";
			List<String> addSql = StringUtils.findRegular(str,addSqlFieldRegular);
			if(addSql.size() != 1) {
				throw new t.backstage.error.BusinessException(5011,sql,addSqlFieldRegular,addSql.size());
			}
			String fieldRegular = "NOTNULL->\\s.*?(->)";
			List<String> field  = StringUtils.findRegular(str,fieldRegular);
			if(field.size() != 1) {
				throw new t.backstage.error.BusinessException(5011,sql,fieldRegular,field.size());
			}
			// 当前的字段名称
			String fie = field.get(0).replace("NOTNULL->","").replace("->","").trim();
			// 当前要添加的sql
			String sq  = addSql.get(0).replace("{","").replace("}","");
			// 如果不等于空,且不等于null那么就去掉注释添加SQL
			if(j.get(fie) == null || "".equals(j.get(fie))) {
				sql=sql.replace(str,"");
			}else {
				sql=sql.replace(str,sq);
				
			}
		} 
		return sql;
	}
}

