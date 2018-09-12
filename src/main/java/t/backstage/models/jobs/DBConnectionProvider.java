package t.backstage.models.jobs;

import java.sql.Connection;
import java.sql.SQLException;

import org.quartz.utils.ConnectionProvider;

/***
 * 在数据库中管理JOB的工具类
 * @author zhangj
 * @date 2018年9月12日 上午8:44:42
 * @email zhangjin0908@hotmail.com
 */
public class DBConnectionProvider implements ConnectionProvider{

	@Override
	public Connection getConnection() throws SQLException {
		t.sql.SessionFactory sessionFactory = t.backstage.models.context.SpringUtils.getApplicationContext().getBean(t.sql.SessionFactory.class);
		return sessionFactory.openConnection();
	}

	@Override
	public void shutdown() throws SQLException {
	}

	@Override
	public void initialize() throws SQLException {
	}

}

