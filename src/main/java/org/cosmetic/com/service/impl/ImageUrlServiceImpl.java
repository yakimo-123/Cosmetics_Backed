package org.cosmetic.com.service.impl;

import lombok.AllArgsConstructor;
import org.cosmetic.com.enums.ImageType;
import org.cosmetic.com.model.ImageUrl;
import org.cosmetic.com.repository.ImageUrlRepository;
import org.cosmetic.com.service.ImageUrlService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class ImageUrlServiceImpl implements ImageUrlService {

    private ImageUrlRepository imageUrlRepository;

    @Override
    public List<ImageUrl> getAllImages() {
        return List.of();
    }

    @Override
    public List<ImageUrl> findByImageTypeAndId(ImageType imageType, Long id) {
        return imageUrlRepository.findAllByIdAndImageType(id, imageType);
    }

    @Override
    public ImageUrl findById(Long id) {
        return null;
    }

    @Override
    public ImageUrl save(ImageUrl imageUrl) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }
}
