package org.cosmetic.com.service.impl;

import lombok.AllArgsConstructor;
import org.cosmetic.com.dto.request.ProductRequestDto;
import org.cosmetic.com.enums.ImageType;
import org.cosmetic.com.mapper.ProductMapper;
import org.cosmetic.com.model.ImageUrl;
import org.cosmetic.com.model.Product;
import org.cosmetic.com.repository.ProductRepository;
import org.cosmetic.com.service.ImgUrlService;
import org.cosmetic.com.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;
    private ProductMapper productMapper;
    private ImgUrlService imgUrlService;


    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public Product save(ProductRequestDto productRequestDto, List<MultipartFile> images) throws IOException{
        Product product = productMapper.toEntity(productRequestDto);
        product = productRepository.save(product);

        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                String imageUrlInS2 = imgUrlService.saveImageInS2(image);
                ImageUrl imageUrl = ImageUrl.builder()
                        .url(imageUrlInS2)
                        .imageType(ImageType.PRODUCT_IMAGE)
                        .id(product.getId())
                        .build();
                imgUrlService.saveImageUrl(imageUrl);
            }
        }
        return product;
    }

    @Override
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public Product update(Long id, ProductRequestDto product, List<MultipartFile> images) throws IOException {
        Optional<Product> existingProduct = productRepository.findById(id);
        if (existingProduct.isPresent()) {
            Product updatedProduct = productMapper.toEntity(product);
            updatedProduct.setId(id);
            updatedProduct = productRepository.save(updatedProduct);

            if (images != null && !images.isEmpty()) {
                for (MultipartFile image : images) {
                    String imageUrlInS2 = imgUrlService.saveImageInS2(image);
                    ImageUrl imageUrl = ImageUrl.builder()
                            .url(imageUrlInS2)
                            .imageType(ImageType.PRODUCT_IMAGE)
                            .id(updatedProduct.getId())
                            .build();
                    imgUrlService.saveImageUrl(imageUrl);
                }
            }
            return updatedProduct;
        }
        return null;
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
}