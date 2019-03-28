package ro.sci.ems;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ro.sci.ems.dao.EmployeeDAO;
import ro.sci.ems.dao.db.JdbcTemplateEmployeeDAO;
import ro.sci.ems.service.EmployeeService;

import javax.sql.DataSource;

@Configuration
public class ApplicationConfiguration {

	@Value("${db.host}")
	private String dbHost;

	@Value("${db.password}")
	private String dbPassword;

	@Value("${db.user}")
	private String dbUser;

	@Value("${db.name}")
	private String dbName;

	/*@Bean
	public EmployeeService employeeService() {
		EmployeeService ems = new EmployeeService();
		
		ems.setDao(employeeDAO());
		return ems;
	}*/
	
	/*@Bean
	public EmployeeDAO employeeDAO() {
		return new IMEmployeeDAO();
	}

*/

/*		@Bean
   public EmployeeDAO employeeDAO() {
		return new JDBCEmployeeDAO(dbHost,
				"5432",
				dbName,
				dbUser ,
				dbPassword);
   }*/

/*	@Bean
	public EmployeeDAO employeeDAO() {
		return new JdbcTemplateEmployeeDAO(dataSource());
	}*/

	@Bean
	public DataSource dataSource() {
		String url = new StringBuilder()
				.append("jdbc:")
				.append("postgresql")
				.append("://")
				.append(dbHost)
				.append(":")
				.append("5432")
				.append("/")
				.append(dbName)
				.append("?user=")
				.append(dbUser)
				.append("&password=")
				.append(dbPassword).toString();

		return new SingleConnectionDataSource(url, false);
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
