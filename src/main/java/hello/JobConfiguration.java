package hello;


import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class JobConfiguration {

	@Autowired
	CustomerRepository repository;
	
	@Autowired
	public StepBuilderFactory stepBuilders;

	@Autowired
	public JobBuilderFactory jobBuilders;

	@Autowired
	public BatchConfiguration batchConfig;
	
	@Autowired
	public Environment environment;
	
	@Bean
	public Job executeVersionSyncJob() {
		return jobBuilders.get("executeMainJob")
				.incrementer(new RunIdIncrementer())
				.start(executeJob())
				.build();
		
	}
	@Bean
	public Step executeJob() {
		return stepBuilders.get("executeJob")
				.allowStartIfComplete(true)
				.tasklet(new Tasklet() {
					
					@Override
					public RepeatStatus execute(StepContribution contribution,
							ChunkContext chunkContext) throws Exception {
						Customer amy = new Customer("Amy", "Tou");
						Customer tom = new Customer("Tom", "Tou");
						repository.save(amy);
						repository.save(tom);
						Iterable<Customer> findAll = repository.findAll();
						System.out.println(findAll);
						Customer findOne = repository.findOne((long) 1);
						System.out.println("find one" + findOne);
						List<Customer> findByFirstname = repository.findByFirstName("Amy");
						System.out.println(findByFirstname);
						return RepeatStatus.FINISHED;
					}
				})
                .build();
	}
}
