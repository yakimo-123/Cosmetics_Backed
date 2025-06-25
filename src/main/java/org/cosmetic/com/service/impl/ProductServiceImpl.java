package org.cosmetic.com.service.impl;

import lombok.AllArgsConstructor;
import org.cosmetic.com.dto.request.ProductRequestDto;
import org.cosmetic.com.enums.ImageType;
import org.cosmetic.com.enums.ProductStatus;
import org.cosmetic.com.exception.AppException;
import org.cosmetic.com.exception.ErrorCode;
import org.cosmetic.com.mapper.ProductMapper;
import org.cosmetic.com.model.*;
import org.cosmetic.com.repository.*;
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
    private InventoryService inventoryService;


    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public Product save(ProductRequestDto productRequestDto, List<MultipartFile> images) throws IOException {
        Product product = productMapper.toEntity(productRequestDto);

        List<Category> categories = categoryRepository.findAllById(productRequestDto.getCategoryIds());
        if (categories.size() != productRequestDto.getCategoryIds().size()) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        product.setCategories(categories);
        // Set supplier
        Supplier supplier = supplierRepository.findById(productRequestDto.getSupplierId())
                .orElseThrow(() -> new AppException(ErrorCode.SUPPLIER_NOT_FOUND));
        product.setSupplier(supplier);
        // Set brand
        Brand brand = brandRepository.findById(productRequestDto.getBrandId())
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));
        product.setBrand(brand);


        int quantity = productRequestDto.getQuantity();
        if (quantity < 0) {
            throw new AppException(ErrorCode.INVALID_PRODUCT_QUANTITY);
        }
        //Save product to get product id
        product = productRepository.save(product);
        Inventory inventory = inventoryService.getOrCreateInventory(product.getId(), quantity);
        product.setInventory(inventory);

        boolean isFirstImageAddEd = false;
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                if (!isFirstImageAddEd) {
                    isFirstImageAddEd = true;
                    product.setImageUrl(imgUrlService.saveImageInS2(image));
                }
                String imageUrlInS2 = imgUrlService.saveImageInS2(image);
                ImageUrl imageUrl = ImageUrl.builder()
                        .url(imageUrlInS2)
                        .imageType(ImageType.PRODUCT_IMAGE)
                        .id(product.getId())
                        .build();
                imgUrlService.saveImageUrl(imageUrl);
            }
        }
        return productRepository.save(product);
    }

    @Override
    public void deleteById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        product.setProductStatus(ProductStatus.DISCONTINUED);
        productRepository.save(product);
    }

    @Override
    public Product update(Long id, ProductRequestDto productDto, List<MultipartFile> images) throws IOException {
        productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        Product updatedProduct = productMapper.toEntity(productDto);
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

    @Override
    public Page<Product> findAllProductNotInProductStatusDISCONTINUED(Pageable pageable) {
        return productRepository.findByProductStatusNotIn(List.of(ProductStatus.DISCONTINUED), pageable);
    }
}