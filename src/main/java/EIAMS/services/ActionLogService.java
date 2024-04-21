package EIAMS.services;

import EIAMS.entities.ActionLog;
import EIAMS.repositories.ActionLogRepository;
import EIAMS.services.interfaces.ActionLogServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ActionLogService implements ActionLogServiceInterface {
    @Autowired
    ActionLogRepository actionLogRepository;

    @Override
    public Page<ActionLog> list(Integer page, Integer limit, Integer semesterId , String userName) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("id").descending());
        return actionLogRepository.findByDynamic(semesterId, userName, pageable);
    }
}
