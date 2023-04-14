package pro.mbroker.app.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Pagination {
    private int page;
    private int size;
    private String sortBy;
    private String sortOrder;
}
