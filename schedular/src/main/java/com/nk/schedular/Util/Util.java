package com.nk.schedular.Util;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.nk.schedular.constants.ApiConstants;
import com.nk.schedular.enums.SortOrder;
import com.nk.schedular.exception.BadRequestException;

public abstract class Util {

    private Util() {}
/**
     * util method to get Pageable object.
     * @param page page no
     * @param pageSize page size
     * @param sort shot order asc desc or none
     * @param sortBy shot by field name
     * @return Pageable object
     */
    public static Pageable getPageable(int page, int pageSize, String sort, String sortBy) {
        if (page < 1) {
            page = 1;
        }
        if(pageSize < 1) {
            throw new BadRequestException("Page size cannot be less than 1");
        }
        if(null == sort) {
            throw new BadRequestException("Invalid sort parameter: null. Must be 'ASC' or 'DESC'.");
        }
        if(null == sortBy) {
            throw new BadRequestException("Invalid sortBy parameter: null. Must be a valid field name.");
        }
        if(sort.equals(ApiConstants.NO_SORT) && sortBy.equals(ApiConstants.NO_SORT)) {
            return PageRequest.of(page - 1, pageSize);
        } else if(sort.equals(SortOrder.DESC.name())) {
            return PageRequest.of(page - 1, pageSize, Sort.by(sortBy).descending());
        } else {
            return PageRequest.of(page - 1, pageSize, Sort.by(sortBy).ascending());
        }
    }

    public static PagePageSizeRecord getResult(Integer page, Integer pageSize) {
        if (page == null || pageSize == null) {
            page = ApiConstants.DEFAULT_PAGE;
            pageSize = ApiConstants.DEFAULT_PAGE_SIZE;
        }
        return new PagePageSizeRecord(page, pageSize);
    }

    public record PagePageSizeRecord(Integer page, Integer pageSize) {
    }
    public static LocalDateTime getCurrentTimestamp() {
        return LocalDateTime.now();
    }
}
