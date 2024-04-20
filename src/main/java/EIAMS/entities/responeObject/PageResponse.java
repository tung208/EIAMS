package EIAMS.entities.responeObject;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PageResponse <T>{
    private int currentPage;
    private int totalPages;
    private int size;
    private long totalElement;
    private List<T> dataList;
}
