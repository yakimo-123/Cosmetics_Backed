package org.cosmetic.com.repository;


import org.cosmetic.com.enums.ImageType;
import org.cosmetic.com.model.ImageUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageUrlRepository extends JpaRepository<ImageUrl, Long> {
    List<ImageUrl> findAllByIdAndImageType(Long id, ImageType imageType);
}
