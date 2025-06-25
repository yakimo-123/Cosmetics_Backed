package org.cosmetic.com.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SupplierRequestDto {
    @NotBlank(message = "Supplier name is required")
    @Size(max = 100, message = "Supplier name must be at most 100 characters")
    private String name;

    @Size(max = 100, message = "Contact person must be at most 100 characters")
    private String contactPerson;

    @Size(max = 20, message = "Phone number must be at most 20 characters")
    private String phone;

    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must be at most 100 characters")
    private String email;

    @Size(max = 255, message = "Address must be at most 255 characters")
    private String address;
}
