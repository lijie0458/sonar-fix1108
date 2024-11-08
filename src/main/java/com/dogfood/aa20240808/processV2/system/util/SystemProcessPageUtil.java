package com.dogfood.aa20240808.processV2.system.util;

import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.api.query.Query;
import org.flowable.common.engine.api.query.QueryProperty;

import java.util.List;
import java.util.Map;

/**
 * A util class that can be used to perform easy pagination (with defaults provided by Flowable).
 */
public interface SystemProcessPageUtil {

    /**
    * Uses the pagination attributes perform the querying and render the response.
    * page >= 1
    * @param paginateRequest The paginated request that is used to get the pagination parameters from
    * @param query The query to get the paged list from
    * @param defaultSort The default sort column (the rest attribute) that later will be mapped to an internal engine name
    * @param properties The sort properties
    */
    static <RES> PageOf<RES> paginateQueryV2(Pageable paginateRequest, Query<?, RES> query, String defaultSort, Map<String, QueryProperty> properties) {
        // Use defaults for paging, if not set in the PaginationRequest, nor in the URL
        int pageNumber = getPageNumber(paginateRequest.getPageNumber(), 1);

        int size = getPageSize(paginateRequest.getPageSize());

        String sortProperty = StringUtils.isBlank(paginateRequest.getSortProperty()) ? defaultSort : paginateRequest.getSortProperty();
        String orderDirection = StringUtils.isBlank(paginateRequest.getSortDirection()) ? "asc" : paginateRequest.getSortDirection();


        // Sort order
        if (isSortOrderValid(sortProperty, properties)) {
            QueryProperty queryProperty = properties.get(sortProperty);
            if (queryProperty == null) {
                throw new IllegalArgumentException("Value for param 'sort' is not valid, '" + sortProperty + "' is not a valid property");
            }

            query.orderBy(queryProperty);
            if (orderDirection.equals("asc")) {
                query.asc();
            } else if (orderDirection.equals("desc")) {
                query.desc();
            } else {
                throw new IllegalArgumentException("Value for param 'order' is not valid : '" + orderDirection + "', must be 'asc' or 'desc'");
            }
        }

        int offset = (pageNumber - 1) * size;
        // Get result and set pagination parameters
        List<RES> list = query.listPage(offset, size);

        long count;
        if (pageNumber == 1 && list.size() < size) {
           count = list.size();
        } else {
           count = query.count();
        }

        return PageOf.of(list, count, pageNumber, size);
    }

    static int getPageNumber(Integer pageNumber, int defaultPageNumber) {
        return pageNumber == null || pageNumber < defaultPageNumber ? defaultPageNumber : pageNumber;
    }

    static int getPageSize(Integer pageSize) {
        return pageSize == null || pageSize < 0 ? 10 : pageSize;
    }

    static boolean isSortOrderValid(String sortProperty, Map<String, QueryProperty> properties) {
        return sortProperty != null && properties != null && !properties.isEmpty();
    }

    public static Pageable createPageable(Integer page, Integer size, String sort, String order) {
        Pageable pageable = new Pageable();
        pageable.setPageNumber(page);
        pageable.setPageSize(size);
        pageable.setSortProperty(order);
        pageable.setSortDirection(sort);
        return pageable;
    }
}
