package org.cosmetic.com.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.cosmetic.com.exception.ErrorCode;

@Builder
@Getter
@Setter
public class ApiResponse<T> {
    private boolean status;
    private int code;
    private String message;
    private T data;

    /* Factory helper: thành công */
    public static <T> ApiResponse<T> success(T data, String msg) {
        return ApiResponse.<T>builder()
                .status(true).code(0).message(msg).data(data).build();
    }

    /* Factory helper: lỗi */
    public static <T> ApiResponse<T> error(ErrorCode ec) {
        return ApiResponse.<T>builder()
                .status(false).code(ec.getCode()).message(ec.getMessage()).data(null).build();
    }

    /* Lỗi với message tuỳ chỉnh (giữ nguyên code, đổi message) */
    public static <T> ApiResponse<T> error(ErrorCode ec, String customMsg) {
        return ApiResponse.<T>builder()
                .status(false).code(ec.getCode()).message(customMsg).data(null).build();
    }

}