package t.backstage.models.sql;


import org.junit.Test;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

/***
 * 
 * @author zhangj
 * @date 2018年9月18日 下午2:02:01
 * @email zhangjin0908@hotmail.com
 */
public class TestSql {
	@Test
	public void testFindWhere() throws JSQLParserException {
		Select select = (Select) CCJSqlParserUtil.parse("select * from user_tab_comments where table_name ='123' and table_name ='2'");
		PlainSelect where = (PlainSelect) select.getSelectBody();
		System.out.println(where.getWhere());
	}
}

