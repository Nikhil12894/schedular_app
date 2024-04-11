package com.nk.schedular.Util;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.nk.schedular.constants.ApiConstants;
import com.nk.schedular.dto.SortOrder;
import com.nk.schedular.exception.BadRequestException;

public abstract class Util {

    private Util() {}
/**
     * util method to get Pageable object.
     * @param page
     * @param pageSize
     * @param sort
     * @param sortBy
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
            throw new BadRequestException("Invalid sort parameter, must be ASC or DESC");
        }
        if(null == sortBy) {
            throw new BadRequestException("Invalid sortby parameter");
        }
        Pageable pageable = null;
        if(sort.equals(ApiConstants.NO_SORT) && sortBy.equals(ApiConstants.NO_SORT)) {
            pageable = PageRequest.of(page - 1, pageSize);
        } else if(sort.equals(SortOrder.DESC.name())) {
            pageable = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).descending());
        } else {
            pageable = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).ascending());
        }
        return pageable;
    }
    public static LocalDateTime getCurrentTimestamp() {
        return LocalDateTime.now();
    }
}