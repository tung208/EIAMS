package EIAMS.jobs.student;

import EIAMS.entities.Student;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@EnableBatchProcessing
public class ImportStudent {

    @Bean
    public FlatFileItemReader<Student> csvStudentReader() {
        FlatFileItemReader<Student> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("students.csv"));
        reader.setLineMapper(new DefaultLineMapper<Student>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames("subjectCode", "rollNumber", "memberCode", "fullName", "cmtnd", "semesterId", "blackList");
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<Student>() {{
                setTargetType(Student.class);
            }});
        }});
        return reader;
    }

    @Bean
    public ItemProcessor<Student, Student> csvStudentProcessor() {
        return item -> {
            // Perform any processing if needed
            return item;
        };
    }

    @Bean
    public ItemWriter<Student> csvStudentWriter() {
        return items -> {
            // Implement logic to write items to the database or perform other actions
        };
    }

    @Bean
    public Job importStudentCsvJob(JobBuilderFactory jobBuilderFactory,
                                   Step csvStudentStep) {
        return jobBuilderFactory.get("importStudentCsvJob")
                .flow(csvStudentStep)
                .end()
                .build();
    }

    @Bean
    public Step csvStudentStep(StepBuilderFactory stepBuilderFactory,
                               ItemReader<Student> csvStudentReader,
                               ItemProcessor<Student, Student> csvStudentProcessor,
                               ItemWriter<Student> csvStudentWriter) {
        return stepBuilderFactory.get("csvStudentStep")
                .<Student, Student>chunk(20)
                .reader(csvStudentReader)
                .processor(csvStudentProcessor)
                .writer(csvStudentWriter)
                .build();
    }
}
