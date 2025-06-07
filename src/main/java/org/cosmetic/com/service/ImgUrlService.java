
package org.cosmetic.com.service;

import org.cosmetic.com.model.ImageUrl;
import java.util.List;

public interface ImgUrlService {
    ImageUrl saveImageUrl(ImageUrl imageUrl);
    ImageUrl getImageUrlById(Long id);
    List<ImageUrl> getAllImageUrls();
    void deleteImageUrl(Long id);
}