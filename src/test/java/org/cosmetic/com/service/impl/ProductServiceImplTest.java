package org.cosmetic.com.service.impl;

import org.cosmetic.com.dto.request.ProductRequestDto;
import org.cosmetic.com.enums.ProductStatus;
import org.cosmetic.com.exception.AppException;
import org.cosmetic.com.mapper.ProductMapper;
import org.cosmetic.com.model.*;
import org.cosmetic.com.repository.*;
import org.cosmetic.com.service.ImgUrlService;
import org.cosmetic.com.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock private ProductRepository productRepository;
    @Mock private ProductMapper productMapper;
    @Mock private ImgUrlService imgUrlService;
    @Mock private CategoryRepository categoryRepository;
    @Mock private SupplierRepository supplierRepository;
    @Mock private BrandRepository brandRepository;
    @Mock private InventoryRepository inventoryRepository;
    @Mock private InventoryService inventoryService;

    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        productService = new ProductServiceImpl(
            productRepository, productMapper, imgUrlService, categoryRepository,
            supplierRepository, brandRepository, inventoryRepository, inventoryService
        );
    }

    @Nested
    @DisplayName("Find All Products Tests")
    class FindAllTests {
        
        @Test
        @DisplayName("Should return all products")
        void shouldReturnAllProducts() {
            // Given
            List<Product> products = Arrays.asList(new Product(), new Product());
            when(productRepository.findAll()).thenReturn(products);

            // When
            List<Product> result = productService.findAll();

            // Then
            assertEquals(2, result.size());
            verify(productRepository).findAll();
        }
    }

    @Nested
    @DisplayName("Find By Id Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return product when found")
        void shouldReturnProductWhenFound() {
            // Given
            Long productId = 1L;
            Product product = new Product();
            product.setId(productId);
            when(productRepository.findById(productId)).thenReturn(Optional.of(product));

            // When
            Optional<Product> result = productService.findById(productId);

            // Then
            assertTrue(result.isPresent());
            assertEquals(productId, result.get().getId());
        }

        @Test
        @DisplayName("Should return empty when product not found")
        void shouldReturnEmptyWhenProductNotFound() {
            // Given
            Long productId = 1L;
            when(productRepository.findById(productId)).thenReturn(Optional.empty());

            // When
            Optional<Product> result = productService.findById(productId);

            // Then
            assertFalse(result.isPresent());
        }
    }

    @Nested
    @DisplayName("Save Product Tests")
    class SaveProductTests {

        @Test
        @DisplayName("Should successfully save product with images")
        void shouldSaveProductWithImages() throws IOException {
            // Given
            ProductRequestDto requestDto = new ProductRequestDto();
            requestDto.setCategoryIds(List.of(1L));
            requestDto.setSupplierId(1L);
            requestDto.setBrandId(1L);
            requestDto.setQuantity(10);

            Product product = new Product();
            product.setId(1L);
            
            List<Category> categories = List.of(new Category());
            Supplier supplier = new Supplier();
            Brand brand = new Brand();
            Inventory inventory = new Inventory();
            
            MockMultipartFile image = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "test".getBytes()
            );

            when(productMapper.toEntity(requestDto)).thenReturn(product);
            when(categoryRepository.findAllById(any())).thenReturn(categories);
            when(supplierRepository.findById(any())).thenReturn(Optional.of(supplier));
            when(brandRepository.findById(any())).thenReturn(Optional.of(brand));
            when(productRepository.save(any(Product.class))).thenReturn(product);
            when(inventoryService.getOrCreateInventory(any(), anyInt())).thenReturn(inventory);
            when(imgUrlService.saveImageInS2(any())).thenReturn("image-url");

            // When
            Product savedProduct = productService.save(requestDto, List.of(image));

            // Then
            assertNotNull(savedProduct);
            verify(productRepository, times(2)).save(any(Product.class));
            verify(imgUrlService, times(2)).saveImageInS2(any());
            verify(imgUrlService).saveImageUrl(any());
        }

        @Test
        @DisplayName("Should throw exception when category not found")
        void shouldThrowExceptionWhenCategoryNotFound() {
            // Given
            ProductRequestDto requestDto = new ProductRequestDto();
            requestDto.setCategoryIds(List.of(1L, 2L));
            when(categoryRepository.findAllById(any())).thenReturn(List.of(new Category()));

            // When & Then
            assertThrows(AppException.class, () -> productService.save(requestDto, null));
        }
    }

    @Nested
    @DisplayName("Delete Product Tests")
    class DeleteProductTests {

        @Test
        @DisplayName("Should mark product as discontinued")
        void shouldMarkProductAsDiscontinued() {
            // Given
            Long productId = 1L;
            Product product = new Product();
            product.setId(productId);
            
            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            when(productRepository.save(any(Product.class))).thenReturn(product);

            // When
            productService.deleteById(productId);

            // Then
            assertEquals(ProductStatus.DISCONTINUED, product.getProductStatus());
            verify(productRepository).save(product);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent product")
        void shouldThrowExceptionWhenDeletingNonExistentProduct() {
            // Given
            Long productId = 1L;
            when(productRepository.findById(productId)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(AppException.class, () -> productService.deleteById(productId));
        }
    }

    @Nested
    @DisplayName("Update Product Tests")
    class UpdateProductTests {

        @Test
        @DisplayName("Should successfully update product with images")
        void shouldUpdateProductWithImages() throws IOException {
            // Given
            Long productId = 1L;
            ProductRequestDto productDto = new ProductRequestDto();
            Product product = new Product();
            MockMultipartFile image = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "test".getBytes()
            );

            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            when(productMapper.toEntity(productDto)).thenReturn(product);
            when(productRepository.save(any(Product.class))).thenReturn(product);
            when(imgUrlService.saveImageInS2(any())).thenReturn("image-url");

            // When
            Product updatedProduct = productService.update(productId, productDto, List.of(image));

            // Then
            assertNotNull(updatedProduct);
            verify(productRepository).save(any(Product.class));
            verify(imgUrlService).saveImageInS2(any());
            verify(imgUrlService).saveImageUrl(any());
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent product")
        void shouldThrowExceptionWhenUpdatingNonExistentProduct() {
            // Given
            Long productId = 1L;
            ProductRequestDto productDto = new ProductRequestDto();
            when(productRepository.findById(productId)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(AppException.class, 
                () -> productService.update(productId, productDto, null));
        }
    }

    @Nested
    @DisplayName("Find Active Products Tests")
    class FindActiveProductsTests {

        @Test
        @DisplayName("Should return active products page")
        void shouldReturnActiveProductsPage() {
            // Given
            List<Product> products = Arrays.asList(new Product(), new Product());
            Page<Product> productPage = new PageImpl<>(products);
            Pageable pageable = Pageable.unpaged();

            when(productRepository.findByProductStatusNotIn(any(), any())).thenReturn(productPage);

            // When
            Page<Product> result = productService.findAllProductNotInProductStatusDISCONTINUED(pageable);

            // Then
            assertNotNull(result);
            assertEquals(2, result.getContent().size());
            verify(productRepository).findByProductStatusNotIn(any(), any());
        }
    }
}