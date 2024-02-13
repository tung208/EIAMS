package EIAMS.jobs.student;

import EIAMS.entities.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

public class StudentItemWriter implements ItemWriter<Student> {
    private static final Logger log = LoggerFactory.getLogger(StudentItemWriter.class);

    @Override
    public void write(Chunk<? extends Student> chunk) throws Exception {

    }
}
