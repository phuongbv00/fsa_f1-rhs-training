package fsa.f1rhstraining.job.postdel;

import fsa.f1rhstraining.dto.PostDto;
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
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.validation.BindException;

import java.nio.file.Files;

@Configuration
public class PostDelJobConfig {
    @Bean("postDelJob")
    public Job postDelJob(JobRepository jobRepository,
                          @Qualifier("postDelJob_step1") Step step1,
                          @Qualifier("postDelJob_step2") Step step2) {
        return new JobBuilder("postDel", jobRepository)
                .start(step1)
                .next(step2)
                .build();
    }

    @Bean("postDelJob_step1")
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                      @Qualifier("postDelJob_step1_itemReader") ItemReader<PostDto> itemReader,
                      @Qualifier("postDelJob_step1_itemProcessor") ItemProcessor<PostDto, String> itemProcessor,
                      @Qualifier("postDelJob_step1_itemWriter") ItemWriter<String> itemWriter) {
        return new StepBuilder("postDelJob_step1", jobRepository)
                .<PostDto, String>chunk(10, transactionManager)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();
    }

    @Bean("postDelJob_step1_itemReader")
    public ItemReader<PostDto> itemReader(@Value("${job.postdel.step1.in}") Resource resource) {
        DefaultLineMapper<PostDto> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(new DelimitedLineTokenizer());
        lineMapper.setFieldSetMapper(new FieldSetMapper<PostDto>() {
            @Override
            public PostDto mapFieldSet(FieldSet fs) throws BindException {
                return PostDto.builder()
                        .id(fs.readLong(0))
                        .title(fs.readString(1))
                        // TODO: add more fields if needed
                        .build();
            }
        });

        return new FlatFileItemReaderBuilder<PostDto>()
                .name("postDelJob_step1_itemReader")
                .resource(resource)
                .linesToSkip(1)
                .lineMapper(lineMapper)
                .build();
    }

    @Bean("postDelJob_step1_itemProcessor")
    public ItemProcessor<PostDto, String> itemProcessor() {
        return new ItemProcessor<PostDto, String>() {
            @Override
            public String process(PostDto item) throws Exception {
                if (item.getTitle().startsWith("T"))
                    return item.toString();
                return null;
            }
        };
    }

    @Bean("postDelJob_step1_itemWriter")
    public ItemWriter<String> itemWriter(@Value("${job.postdel.step1.out}") WritableResource resource) {
        return new FlatFileItemWriterBuilder<String>()
                .name("postDelJob_step1_itemWriter")
                .resource(resource)
                .lineAggregator(new PassThroughLineAggregator<>())
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
                        Files.copy(in.getFile().toPath(), out.getFile().toPath());
                        return RepeatStatus.FINISHED;
                    }
                }, transactionManager)
                .build();
    }
}
