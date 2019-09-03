package ro.sci.ems;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;
import java.sql.SQLException;

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

	@Bean
	public DataSource dataSource() throws SQLException {

	    // connect to PostgreSQL
//		String url = new StringBuilder()
//				.append("jdbc:")
//				.append("postgresql")
//				.append("://")
//				.append(dbHost)
//				.append(":")
//				.append("5432")
//				.append("/")
//				.append(dbName)
//				.append("?user=")
//				.append(dbUser)
//				.append("&password=")
//				.append(dbPassword).toString();


        // connect to Oracle SQL
		String url = new StringBuilder()
				.append("jdbc:")
				.append("oracle:thin")
				.append(":@//")
				.append(dbHost)
				.append(":")
				.append("1521")
				.append("/")
				.append("xe").toString();

		return new SingleConnectionDataSource(url, dbName, dbPassword, true);
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
