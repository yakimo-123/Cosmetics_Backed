package org.cosmetic.com.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    /* ───────────── 1000-Range: AUTH / ACCOUNT ───────────── */
    INVALID_CREDENTIALS      (1001, "Invalid email or password",                 HttpStatus.UNAUTHORIZED),
    INVALID_ACCESS_TOKEN     (1002, "Invalid access token",                      HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN    (1003, "Invalid refresh token",                     HttpStatus.UNAUTHORIZED),
    INVALID_OTP              (1004, "Invalid OTP",                               HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_EXISTS     (1005, "Email already exists",                      HttpStatus.BAD_REQUEST),
    EMAIL_INVALID_FORMAT     (1006, "Invalid email format",                      HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND           (1007, "User not found",                            HttpStatus.NOT_FOUND),
    USER_ALREADY_VERIFIED    (1008, "Account already verified",                  HttpStatus.BAD_REQUEST),

    /* ───────────── 1100-Range: USER PROFILE ───────────── */
    EMAIL_EXISTS             (1103, "Email is already in use",                   HttpStatus.BAD_REQUEST),

    /* ───────────── 2000-Range: BRAND ───────────── */
    BRAND_NOT_FOUND          (2001, "Brand not found",                           HttpStatus.NOT_FOUND),

    /* ───────────── 3000-Range: CART / PRODUCT ───────────── */
    CART_NOT_FOUND           (3001, "Cart not found",                            HttpStatus.NOT_FOUND),
    PRODUCT_NOT_FOUND        (3002, "Product not found",                         HttpStatus.NOT_FOUND),
    CART_ITEM_NOT_FOUND      (3003, "Cart item not found",                       HttpStatus.NOT_FOUND),
    ACTIVE_CART_NOT_FOUND    (3004, "Active cart not found",                     HttpStatus.NOT_FOUND),

    /* ───────────── 4000-Range: CATEGORY ───────────── */
    CATEGORY_NOT_FOUND       (4001, "Category not found",                        HttpStatus.NOT_FOUND),

    /* ───────────── 5000-Range: EMAIL ───────────── */
    EMAIL_SEND_FAILED        (5001, "Unable to send email",                      HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_VERIFICATION_FAILED(5002, "Failed to send verification email",        HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_FORGOT_FAILED      (5003, "Failed to send password reset email",      HttpStatus.INTERNAL_SERVER_ERROR),

    /* ───────────── 6000-Range: INVENTORY ───────────── */
    INVENTORY_NOT_FOUND      (6001, "Inventory not found",                       HttpStatus.NOT_FOUND),
    PRODUCT_ID_REQUIRED      (6002, "Product ID is required",                    HttpStatus.BAD_REQUEST),

    /* ───────────── 7000-Range: ORDER ───────────── */
    ORDER_NOT_FOUND          (7001, "Order not found",                           HttpStatus.NOT_FOUND),
    INSUFFICIENT_INVENTORY   (7004, "Insufficient product inventory",           HttpStatus.BAD_REQUEST),

    /* ───────────── 8000-Range: OTP ───────────── */
    OTP_INVALID              (8001, "Invalid OTP code",                          HttpStatus.BAD_REQUEST),
    OTP_EXPIRED              (8002, "OTP code has expired",                      HttpStatus.BAD_REQUEST),

    /* ───────────── 9000-Range: SUPPLIER / PRODUCT RULES ───────────── */
    SUPPLIER_NOT_FOUND       (9003, "Supplier not found",                        HttpStatus.NOT_FOUND),
    INVALID_PRODUCT_QUANTITY (9005, "Invalid product quantity",                  HttpStatus.BAD_REQUEST),

    /* ───────────── 10000-Range: REVIEW ───────────── */
    REVIEW_NOT_FOUND         (10001,"Review not found",                          HttpStatus.NOT_FOUND),
    REVIEW_ALREADY_REPLIED   (10002,"Review already has a response",            HttpStatus.BAD_REQUEST),

    /* ───────────── 13000-Range: JWT ───────────── */
    JWT_GENERATION_FAILED    (13001,"Failed to generate JWT",                    HttpStatus.INTERNAL_SERVER_ERROR),
    JWT_MALFORMED            (13002,"Malformed JWT",                             HttpStatus.BAD_REQUEST),
    JWT_SIGNATURE_INVALID    (13003,"Invalid JWT signature",                     HttpStatus.UNAUTHORIZED),
    JWT_EXPIRED              (13004,"JWT has expired",                           HttpStatus.UNAUTHORIZED),

    /* ───────────── 14000-Range: SECURITY / VALIDATION ───────────── */
    VALIDATION_FAILED        (1400, "Invalid data",                              HttpStatus.BAD_REQUEST),
    ACCESS_DENIED            (1403, "Access denied",                             HttpStatus.FORBIDDEN),

    /* ───────────── 15000-Range: COMMON ───────────── */
    INTERNAL_ERROR           (1500, "Internal server error",                     HttpStatus.INTERNAL_SERVER_ERROR);

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
