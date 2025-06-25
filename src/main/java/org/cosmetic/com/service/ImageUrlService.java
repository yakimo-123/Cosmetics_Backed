package org.cosmetic.com.service;


import org.cosmetic.com.enums.ImageType;
import org.cosmetic.com.model.ImageUrl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ImageUrlService {
    List<ImageUrl> getAllImages();

    List<ImageUrl> findByImageTypeAndId(ImageType imageType, Long id);

    ImageUrl findById(Long id);

    ImageUrl save(ImageUrl imageUrl);

    void deleteById(Long id);
}
