package EIAMS.services;

import EIAMS.repositories.ExamCodeRepository;
import EIAMS.services.interfaces.ExamCodeServiceInterface;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ExamCodeService implements ExamCodeServiceInterface {
    
    private final ExamCodeRepository examCodeRepository;
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);
    @Override
    public Integer uploadExamCode(MultipartFile file, int semester_id) throws IOException {
        return null;
    }
}
