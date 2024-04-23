package EIAMS.entities.responeObject;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DashboardResponse {
    @JsonProperty("currentPage")
    private int currentPage;
    @JsonProperty("totalPages")
    private int totalPages;
    @JsonProperty("size")
    private int size;
    @JsonProperty("totalRoom")
    private long totalRoom;
    @JsonProperty("totalStudent")
    private long totalStudent;
    @JsonProperty("data")
    private Object dataList;
}
