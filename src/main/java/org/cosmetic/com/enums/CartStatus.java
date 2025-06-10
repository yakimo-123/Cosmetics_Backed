package org.cosmetic.com.enums;

public enum CartStatus {
    ACTIVE,           // Đang shopping
    ABANDONED,        // User không mua (sau 24h chưa checkout)
    CONVERTED,        // Đã chuyển thành order thành công
    SAVED_FOR_LATER,  // User save để mua sau
    DELETED
}