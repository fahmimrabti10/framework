package query;
import lombok.*;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Request {

    private Map<String, BasicOperator> query;
    private Search search;
    private Map<String, Long> field;
    private Map<String, Long> sort;
    private Integer offset;
    private Integer limit;
    private String lang;

}
