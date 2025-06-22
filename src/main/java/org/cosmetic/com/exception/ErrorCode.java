package org.cosmetic.com.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    /* ───────────── 1000-Range: AUTH / ACCOUNT ───────────── */
    INVALID_CREDENTIALS      (1001, "Email hoặc mật khẩu không đúng",        HttpStatus.UNAUTHORIZED),
    INVALID_ACCESS_TOKEN     (1002, "Access token không hợp lệ",            HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN    (1003, "Refresh token không hợp lệ",           HttpStatus.UNAUTHORIZED),
    INVALID_OTP              (1004, "OTP không hợp lệ",                     HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_EXISTS     (1005, "Email đã tồn tại",                     HttpStatus.BAD_REQUEST),
    EMAIL_INVALID_FORMAT     (1006, "Định dạng email không hợp lệ",         HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND           (1007, "Không tìm thấy người dùng",            HttpStatus.NOT_FOUND),
    USER_ALREADY_VERIFIED    (1008, "Tài khoản đã xác thực",                HttpStatus.BAD_REQUEST),

    /* ───────────── 1100-Range: USER PROFILE ───────────── */
    EMAIL_EXISTS             (1103, "Email đã được sử dụng",                HttpStatus.BAD_REQUEST),

    /* ───────────── 2000-Range: BRAND ───────────── */
    BRAND_NOT_FOUND          (2001, "Không tìm thấy thương hiệu",           HttpStatus.NOT_FOUND),

    /* ───────────── 3000-Range: CART / PRODUCT ───────────── */
    CART_NOT_FOUND           (3001, "Không tìm thấy giỏ hàng",              HttpStatus.NOT_FOUND),
    PRODUCT_NOT_FOUND        (3002, "Không tìm thấy sản phẩm",              HttpStatus.NOT_FOUND),
    CART_ITEM_NOT_FOUND      (3003, "Không tìm thấy sản phẩm trong giỏ hàng",HttpStatus.NOT_FOUND),
    ACTIVE_CART_NOT_FOUND    (3004, "Không tìm thấy giỏ hàng đang hoạt động",HttpStatus.NOT_FOUND),

    /* ───────────── 4000-Range: CATEGORY ───────────── */
    CATEGORY_NOT_FOUND       (4001, "Không tìm thấy danh mục",              HttpStatus.NOT_FOUND),

    /* ───────────── 5000-Range: EMAIL ───────────── */
    EMAIL_SEND_FAILED        (5001, "Không thể gửi email",                  HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_VERIFICATION_FAILED(5002, "Gửi email xác minh thất bại",          HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_FORGOT_FAILED      (5003, "Gửi email đặt lại mật khẩu thất bại",  HttpStatus.INTERNAL_SERVER_ERROR),

    /* ───────────── 6000-Range: INVENTORY ───────────── */
    INVENTORY_NOT_FOUND      (6001, "Không tìm thấy tồn kho",               HttpStatus.NOT_FOUND),
    PRODUCT_ID_REQUIRED      (6002, "Thiếu productId",                      HttpStatus.BAD_REQUEST),

    /* ───────────── 7000-Range: ORDER ───────────── */
    ORDER_NOT_FOUND          (7001, "Không tìm thấy đơn hàng",              HttpStatus.NOT_FOUND),
    INSUFFICIENT_INVENTORY   (7004, "Sản phẩm không đủ tồn kho",            HttpStatus.BAD_REQUEST),

    /* ───────────── 8000-Range: OTP ───────────── */
    OTP_INVALID              (8001, "Mã OTP không hợp lệ",                  HttpStatus.BAD_REQUEST),
    OTP_EXPIRED              (8002, "Mã OTP đã hết hạn",                    HttpStatus.BAD_REQUEST),

    /* ───────────── 9000-Range: SUPPLIER / PRODUCT RULES ───────────── */
    SUPPLIER_NOT_FOUND       (9003, "Không tìm thấy nhà cung cấp",          HttpStatus.NOT_FOUND),
    INVALID_PRODUCT_QUANTITY (9005, "Số lượng sản phẩm không hợp lệ",       HttpStatus.BAD_REQUEST),

    /* ───────────── 10000-Range: REVIEW ───────────── */
    REVIEW_NOT_FOUND         (10001,"Không tìm thấy đánh giá",              HttpStatus.NOT_FOUND),
    REVIEW_ALREADY_REPLIED   (10002,"Đánh giá đã có phản hồi",              HttpStatus.BAD_REQUEST),

    /* ───────────── 13000-Range: JWT ───────────── */
    JWT_GENERATION_FAILED    (13001,"Không thể tạo JWT",                    HttpStatus.INTERNAL_SERVER_ERROR),
    JWT_MALFORMED            (13002,"JWT không hợp lệ",                     HttpStatus.BAD_REQUEST),
    JWT_SIGNATURE_INVALID    (13003,"Chữ ký JWT không hợp lệ",              HttpStatus.UNAUTHORIZED),
    JWT_EXPIRED              (13004,"JWT đã hết hạn",                       HttpStatus.UNAUTHORIZED),

    /* ───────────── 14000-Range: SECURITY / VALIDATION ───────────── */
    VALIDATION_FAILED        (1400, "Dữ liệu không hợp lệ",                 HttpStatus.BAD_REQUEST),
    ACCESS_DENIED            (1403, "Không có quyền truy cập",              HttpStatus.FORBIDDEN),

    /* ───────────── 15000-Range: COMMON ───────────── */
    INTERNAL_ERROR           (1500, "Lỗi máy chủ",                           HttpStatus.INTERNAL_SERVER_ERROR);

    /* ====== fields ====== */
    private final int code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(int code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
