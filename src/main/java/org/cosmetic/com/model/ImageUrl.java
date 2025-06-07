package org.cosmetic.com.model;

import jakarta.persistence.*;
import org.cosmetic.com.enums.ImageType;

@Entity
public class ImageUrl {
    @Id
    private Long id;

    @Column(nullable = false)
    private String url;

    private boolean isPrimary;

    @Enumerated
    @Column(nullable = false)
    private ImageType imageType;
}
