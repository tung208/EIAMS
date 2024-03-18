package EIAMS.helper;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class Pagination {
    public Pageable getPageable(Integer page,Integer limit){
        Pageable pageable;
        if(page != null && limit != null){
            pageable = PageRequest.of(page, limit);
        } else if (page == null && limit != null) {
            pageable = PageRequest.of(0, limit);
        }else if(page != null && limit == null){
            pageable = PageRequest.of(page, 10);
        }else {
            pageable = PageRequest.of(0, Integer.MAX_VALUE);
        }
        return pageable;
    }
}
