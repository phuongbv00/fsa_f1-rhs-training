package fsa.f1rhstraining.job.uc01;

import fsa.f1rhstraining.entity.Post;
import fsa.f1rhstraining.entity.User;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.validation.BindException;

import java.time.LocalDateTime;
import java.util.Map;

@Configuration
public class Uc01JobConfig {
    @Bean("postDelJob")
    public Job postDelJob(JobRepository jobRepository,
                          @Qualifier("postDelJob_step1") Step step1,
                          @Qualifier("postDelJob_step2") Step step2,
                          @Qualifier("postDelJob_step3") Step step3
    ) {
        return new JobBuilder("postDel", jobRepository)
                .start(step1)
                .next(step2)
                .next(step3)
                .build();
    }

    @Bean("postDelJob_step1")
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                      @Qualifier("postDelJob_step1_itemReader") ItemReader<Post> itemReader,
                      @Qualifier("postDelJob_step1_itemProcessor") ItemProcessor<Post, Post> itemProcessor,
                      @Qualifier("postDelJob_step1_itemWriter") ItemWriter<Post> itemWriter) {
        return new StepBuilder("postDelJob_step1", jobRepository)
                .<Post, Post>chunk(10, transactionManager)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();
    }

    @Bean("postDelJob_step1_itemReader")
    public ItemReader<Post> step1_itemReader(@Value("${job.postdel.step1.in}") Resource resource) {
        return new FlatFileItemReaderBuilder<Post>()
                .name("postDelJob_step1_itemReader")
                .resource(resource)
                .linesToSkip(1)
                .lineTokenizer(new DelimitedLineTokenizer())
                .fieldSetMapper(new FieldSetMapper<Post>() {
                    @Override
                    public Post mapFieldSet(FieldSet fs) throws BindException {
                        return Post.builder()
//                                .id(fs.readLong(0))
                                .title(fs.readString(1))
                                .content(fs.readString(2))
                                .author(User.builder().id(fs.readLong(3)).build())
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                // TODO: add more fields if needed
                                .build();
                    }
                })
                .build();
    }

    @Bean("postDelJob_step1_itemProcessor")
    public ItemProcessor<Post, Post> step1_itemProcessor() {
        return new ItemProcessor<Post, Post>() {
            @Override
            public Post process(Post item) throws Exception {
                if (item.getTitle().startsWith("F"))
                    return null;
                return item;
            }
        };
    }

    @Bean("postDelJob_step1_itemWriter")
    public JpaItemWriter<Post> step1_itemWriter(EntityManagerFactory emf) {
        return new JpaItemWriterBuilder<Post>()
                .entityManagerFactory(emf)
                .usePersist(true)
                .build();
    }

    @Bean("postDelJob_step2")
    public Step step2(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                      @Value("${job.postdel.step1.out}") Resource in,
                      @Value("${job.postdel.step2.out}") Resource out) {
        return new StepBuilder("postDelJob_step2", jobRepository)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
//                        Files.copy(in.getFile().toPath(), out.getFile().toPath());
                        return RepeatStatus.FINISHED;
                    }
                }, transactionManager)
                .build();
    }

    @Bean("postDelJob_step3")
    public Step step3(JobRepository jobRepository,
                      PlatformTransactionManager transactionManager,
                      @Qualifier("postDelJob_step3_itemReader") JpaPagingItemReader<Post> itemReader,
                      @Qualifier("postDelJob_step3_itemWriter") ItemWriter<Post> itemWriter
    ) {
        return new StepBuilder("postDelJob_step3", jobRepository)
                .<Post, Post>chunk(10, transactionManager)
                .reader(itemReader)
                .writer(itemWriter)
                .build();
    }

    @Bean("postDelJob_step3_itemReader")
    public JpaPagingItemReader<Post> step3_itemReader(EntityManagerFactory emf) {
        return new JpaPagingItemReaderBuilder<Post>()
                .name("postDelJob_step3_itemReader")
                .entityManagerFactory(emf)
                .pageSize(20)
                .queryString("select p from Post p where p.title like :titleStartWith")
                .parameterValues(Map.of("titleStartWith", "T%"))
                .build();
    }

    @Bean("postDelJob_step3_itemWriter")
    public ItemWriter<Post> step3_itemWriter(@Value("${job.postdel.step1.out}") WritableResource resource) {
        return new FlatFileItemWriterBuilder<Post>()
                .name("postDelJob_step3_itemWriter")
                .resource(resource)
                .lineAggregator(new LineAggregator<Post>() {
                    @Override
                    public String aggregate(Post item) {
                        return item.toString();
                    }
                })
                .build();
    }
}
