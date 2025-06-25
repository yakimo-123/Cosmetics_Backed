package org.cosmetic.com.dto.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class BrandResponseDto implements Serializable {
    private Long id;
    private String name;
}