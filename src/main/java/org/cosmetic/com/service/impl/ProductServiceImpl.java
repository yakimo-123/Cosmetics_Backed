package org.cosmetic.com.service.impl;

import lombok.AllArgsConstructor;
import org.cosmetic.com.dto.request.ProductRequestDto;
import org.cosmetic.com.enums.ImageType;
import org.cosmetic.com.mapper.ProductMapper;
import org.cosmetic.com.model.*;
import org.cosmetic.com.repository.*;
import org.cosmetic.com.service.CategoryService;
import org.cosmetic.com.service.ImgUrlService;
import org.cosmetic.com.service.InventoryService;
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
    private CategoryRepository categoryRepository;
    private SupplierRepository supplierRepository;
    private BrandRepository brandRepository;
    private InventoryRepository inventoryRepository;


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

        List<Category> categories = categoryRepository.findAllById(productRequestDto.getCategoryIds());
        if (categories.size() != productRequestDto.getCategoryIds().size()) {
            throw new IllegalArgumentException("Some categories do not exist");
        }
        product.setCategories(categories);
        // Set supplier
        Supplier supplier = supplierRepository.getReferenceById(productRequestDto.getSupplierId());
        product.setSupplier(supplier);
        // Set brand
        Brand brand = brandRepository.getReferenceById(productRequestDto.getBrandId());
        product.setBrand(brand);

        product = productRepository.save(product);

        // Save inventory
        inventoryRepository.save(Inventory.builder()
                .product(product)
                .quantity(productRequestDto.getQuantity())
                .build());


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