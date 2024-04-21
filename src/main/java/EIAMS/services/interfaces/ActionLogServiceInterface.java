package EIAMS.services.interfaces;

import EIAMS.entities.Account;
import EIAMS.entities.ActionLog;
import org.springframework.data.domain.Page;

public interface ActionLogServiceInterface {
    Page<ActionLog> list(Integer page, Integer limit, Integer semesterId , String name);
}

