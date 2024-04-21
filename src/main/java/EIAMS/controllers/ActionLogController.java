package EIAMS.controllers;

import EIAMS.entities.Account;
import EIAMS.entities.ActionLog;
import EIAMS.entities.Role;
import EIAMS.entities.responeObject.PageResponse;
import EIAMS.services.AccountService;
import EIAMS.services.ActionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/action-log")
public class ActionLogController {
    @Autowired
    ActionLogService actionLogService;
    @GetMapping()
    public PageResponse<ActionLog> list(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "2") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "10") int semesterId,
            @RequestParam(defaultValue = "") String userName
//            @RequestParam(defaultValue = "") String role,
//            @RequestParam(defaultValue = "") String username
    ) {
        Page<ActionLog> page = actionLogService.list(pageNo, pageSize, semesterId, userName);
        return new PageResponse<>(page.getNumber() + 1, page.getTotalPages(), page.getSize(),page.getTotalElements() ,page.getContent());
    }
}
