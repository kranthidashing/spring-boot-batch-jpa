package hello;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableAutoConfiguration(exclude={BatchAutoConfiguration.class})
@EnableTransactionManagement
public class DataSourceConfiguration {
	
	@Bean
	@ConfigurationProperties(prefix="spring.datasource")
	public DataSource routingDataSource() {
	    return DataSourceBuilder.create().build();
	}

}
