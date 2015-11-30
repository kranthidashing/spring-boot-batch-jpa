package hello;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.support.DatabaseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration extends DefaultBatchConfigurer{

	@Autowired
	PlatformTransactionManager transactionManager;

	@Autowired
	DataSourceConfiguration dataSourceConfiguration;
	
	@Autowired
	public Environment environment;
	
	@Bean
	public JobRegistry jobRegistry(){
		return new MapJobRegistry();
	}	
	
	@Bean
	public LobHandler lobHandler(){
		return new DefaultLobHandler();
	}

	@Bean
	public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(){
		JobRegistryBeanPostProcessor beanPostProcessor = new JobRegistryBeanPostProcessor();
		beanPostProcessor.setJobRegistry(jobRegistry());
		return beanPostProcessor;
	}

	@Override
	public JobRepository createJobRepository() throws Exception {
		JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
		factory.setDataSource(dataSourceConfiguration.routingDataSource());
		factory.setTransactionManager(transactionManager);
		factory.setLobHandler(lobHandler());
		factory.setDatabaseType(DatabaseType.fromMetaData(dataSourceConfiguration.routingDataSource()).name());
		factory.setIsolationLevelForCreate("ISOLATION_DEFAULT");
		factory.afterPropertiesSet();
		return  (JobRepository) factory.getObject();
	}
		
	@Bean
	public JobLauncher jobLauncher() throws Exception{
		SimpleJobLauncher launcher = new SimpleJobLauncher();
		launcher.setJobRepository(createJobRepository());
		launcher.setTaskExecutor(new SyncTaskExecutor());
		return launcher;
	}

}
