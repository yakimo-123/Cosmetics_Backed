package org.cosmetic.com.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ApiResponse<T> {
    private boolean status;
    private String message;
    private T data;


}