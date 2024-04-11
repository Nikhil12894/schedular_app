package com.nk.schedular.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class WebResponse<T> {

    private T data;
    private String message;
    private List<String> errors;

    public WebResponse(T data, String message) {
        this.data = data;
        this.message = message;
    }


}
