package org.cosmetic.com.service.impl;

import org.cosmetic.com.model.ImageUrl;
import org.cosmetic.com.repository.ImageUrlRepository;
import org.cosmetic.com.service.ImgUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImgUrlServiceImpl implements ImgUrlService {

    @Autowired
    private ImageUrlRepository imageUrlRepository;

    @Override
    public ImageUrl saveImageUrl(ImageUrl imageUrl) {
        return imageUrlRepository.save(imageUrl);
    }

    @Override
    public ImageUrl getImageUrlById(Long id) {
        return imageUrlRepository.findById(id).orElse(null);
    }

    @Override
    public List<ImageUrl> getAllImageUrls() {
        return imageUrlRepository.findAll();
    }

    @Override
    public void deleteImageUrl(Long id) {
        imageUrlRepository.deleteById(id);
    }
}