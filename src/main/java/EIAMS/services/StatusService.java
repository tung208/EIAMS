package EIAMS.services;

import EIAMS.dtos.StatusDto;
import EIAMS.entities.Status;
import EIAMS.exception.EntityNotFoundException;
import EIAMS.repositories.StatusRepository;
import EIAMS.services.interfaces.StatusServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class StatusService implements StatusServiceInterface {
    @Autowired
    StatusRepository statusRepository;

    @Override
    public void update(int semesterId, int update, int value) throws EntityNotFoundException {
        Optional<Status> status = statusRepository.findBySemesterId(semesterId);
        if(status.isPresent()){
            Status status1 = status.get();
            switch (update) {
                case 1:
                    status1.setPlan_exam(value);
                    break;
                case 2:
                    status1.setSubject(value);
                    break;
                case 3:
                    status1.setRoom(value);
                    break;
                case 4:
                    status1.setLecturer(value);
                    break;
                case 5:
                    status1.setStudent(value);
                    break;
            }
            statusRepository.save(status1);
        } else throw new EntityNotFoundException("Not found status");
    }

    @Override
    public void create(int semesterId) {
        Status status = Status.builder()
                .semesterId(semesterId)
                .plan_exam(0)
                .subject(0)
                .room(0)
                .lecturer(0)
                .student(0)
                .build();
        statusRepository.save(status);
    }

    @Override
    public void delete(int semesterId) {
        statusRepository.deleteBySemesterId(semesterId);
    }

}
