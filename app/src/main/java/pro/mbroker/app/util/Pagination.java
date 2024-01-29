package pro.mbroker.app.util;

import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@NoArgsConstructor
public final class Pagination {
    public static Pageable createPageable(int page, int size, String sortBy, String sortOrder) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        return PageRequest.of(page, size, sort);
    }

    public static Pageable createPageable(int page, int size, Sort sort) {
        return PageRequest.of(page, size, sort);
    }
}
