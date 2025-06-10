package org.cosmetic.com.dto.request;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SupplierRequestDto {
    private String name;
    private String contactPerson;
    private String phone;
    private String email;
    private String address;
}
