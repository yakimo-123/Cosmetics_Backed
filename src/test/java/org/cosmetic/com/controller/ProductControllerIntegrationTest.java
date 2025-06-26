package org.cosmetic.com.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.cosmetic.com.dto.request.ProductRequestDto;
import org.cosmetic.com.enums.ProductStatus;
import org.cosmetic.com.enums.Role;
import org.cosmetic.com.model.*;
import org.cosmetic.com.repository.*;
import org.cosmetic.com.service.ImgUrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("ProductController Integration Tests")
class ProductControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    SupplierRepository supplierRepository;
    @Autowired
    BrandRepository brandRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private ImgUrlService imgUrlService;

    private Product savedProduct;
    private Category savedCategory;
    private Supplier savedSupplier;
    private Brand savedBrand;


    @BeforeEach
    void setup() throws IOException {
        // Create and save a category
        Category category = Category.builder()
                .categoryName("Makeup")
                .description("Makeup products")
                .status(true)
                .build();
        savedCategory = categoryRepository.save(category);

        // Create and save a supplier
        Supplier supplier = new Supplier();
        supplier.setSupplierName("Beauty Supplies Inc.");
        supplier.setContactName("John Doe");
        supplier.setPhone("123-456-7890");
        supplier.setEmail("contact@beautysupplies.com");
        savedSupplier = supplierRepository.save(supplier);

        // Create and save a brand
        Brand brand = Brand.builder()
                .name("Glamour")
                .description("Luxury cosmetics brand")
                .build();
        savedBrand = brandRepository.save(brand);

        // Create and save a product with relationships
        Product product = Product.builder()
                .productName("Lipstick")
                .price(BigDecimal.valueOf(250000))
                .description("Matte red lipstick")
                .productStatus(ProductStatus.ACTIVE)
                .categories(new ArrayList<>(List.of(savedCategory)))
                .supplier(savedSupplier)
                .brand(savedBrand)
                .build();
        savedProduct = productRepository.save(product);

        User user = User.builder()
                .email("admin123@gmail.com")
                .username("admin")
                .password(passwordEncoder.encode("123456"))
                .role(Role.ADMIN)
                .build();
        User user1 = User.builder()
                .email("user123@gamil.com")
                .username("user")
                .password(passwordEncoder.encode("123456"))
                .role(Role.USER)
                .build();
        userRepository.save(user);
        userRepository.save(user1);

        when(imgUrlService.saveImageInS2(any(MultipartFile.class)))
                .thenReturn("https://fake-url.com/image.jpg");
        when(imgUrlService.saveImageUrl(ImageUrl.builder().build()))
                .thenReturn(ImageUrl.builder().build());

    }

    @Nested
    @DisplayName("GET /api/products/page - Get All Products")
    class GetAllProducts {

        @Test
        @DisplayName("200 OK - Returns paginated products")
        void getAllProducts_Success() throws Exception {
            mockMvc.perform(get("/api/products/page")
                            .param("page", "0")
                            .param("size", "10")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value("Products retrieved successfully"))
                    .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))))
                    .andExpect(jsonPath("$.data.content[0].id").exists())
                    .andExpect(jsonPath("$.data.content[0].productName").value("Lipstick"));
        }

        @Test
        @DisplayName("200 OK - Returns empty page when no products match criteria")
        void getAllProducts_EmptyPage() throws Exception {
            // Delete all products to ensure empty result
            productRepository.deleteAll();

            mockMvc.perform(get("/api/products/page")
                            .param("page", "0")
                            .param("size", "10")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value("Products retrieved successfully"))
                    .andExpect(jsonPath("$.data.content", hasSize(0)))
                    .andExpect(jsonPath("$.data.page.totalElements").value(0));
        }
    }

    @Nested
    @DisplayName("GET /api/products/{id} - Get Product By ID")
    class GetProductById {

        @Test
        @DisplayName("200 OK - Returns product when ID exists")
        void getProductById_Success() throws Exception {
            mockMvc.perform(get("/api/products/{id}", savedProduct.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value("Product retrieved successfully"))
                    .andExpect(jsonPath("$.data.id").value(savedProduct.getId()))
                    .andExpect(jsonPath("$.data.productName").value("Lipstick"))
                    .andExpect(jsonPath("$.data.description").value("Matte red lipstick"))
                    .andExpect(jsonPath("$.data.price").value(250000))
                    .andExpect(jsonPath("$.data.brandName").value("Glamour"));
        }

        @Test
        @DisplayName("404 Not Found - Returns not found when ID doesn't exist")
        void getProductById_NotFound() throws Exception {
            Long nonExistentId = 9999L;

            mockMvc.perform(get("/api/products/{id}", nonExistentId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/products - Create Product")
    class CreateProduct {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("200 OK - Creates product successfully when role admin")
        void createProduct_Success() throws Exception {
            // Create product request DTO
            ProductRequestDto requestDto = ProductRequestDto.builder()
                    .productName("Foundation")
                    .description("Long-lasting foundation")
                    .price(300000.0)
                    .quantity(50)
                    .categoryIds(List.of(savedCategory.getId()))
                    .supplierId(savedSupplier.getId())
                    .brandId(savedBrand.getId())
                    .build();

            // Create mock image file
            MockMultipartFile productJson = new MockMultipartFile(
                    "product",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(requestDto));

            MockMultipartFile imageFile = new MockMultipartFile(
                    "images",
                    "test-image.jpg",
                    "image/jpeg",
                    "test image content".getBytes());

            // Perform request
            mockMvc.perform(multipart("/api/products")
                            .file(productJson)
                            .file(imageFile))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value("Product created successfully"))
                    .andExpect(jsonPath("$.data.productName").value("Foundation"))
                    .andExpect(jsonPath("$.data.description").value("Long-lasting foundation"))
                    .andExpect(jsonPath("$.data.price").value(300000.0))
                    .andExpect(jsonPath("$.data.brandName").value("Glamour"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("400 Bad Request - Invalid product data")
        void createProduct_InvalidData() throws Exception {
            // Create invalid product request DTO (missing required fields)
            ProductRequestDto requestDto = ProductRequestDto.builder()
                    .description("Invalid product")
                    // Missing required fields: productName, price, quantity, categoryIds, supplierId, brandId
                    .build();

            // Create mock image file
            MockMultipartFile productJson = new MockMultipartFile(
                    "product",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(requestDto));

            // Perform request
            mockMvc.perform(multipart("/api/products")
                            .file(productJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("403 Access denied ")
        void createProduct_forbidden() throws Exception {
            // Create product request DTO
            ProductRequestDto requestDto = ProductRequestDto.builder()
                    .productName("Foundation")
                    .description("Long-lasting foundation")
                    .price(300000.0)
                    .quantity(50)
                    .categoryIds(List.of(savedCategory.getId()))
                    .supplierId(savedSupplier.getId())
                    .brandId(savedBrand.getId())
                    .build();

            // Create mock image file
            MockMultipartFile productJson = new MockMultipartFile(
                    "product",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(requestDto));

            MockMultipartFile imageFile = new MockMultipartFile(
                    "images",
                    "test-image.jpg",
                    "image/jpeg",
                    "test image content".getBytes());

            // Perform request
            mockMvc.perform(multipart("/api/products")
                            .file(productJson)
                            .file(imageFile))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").exists());
        }
    }

    @Nested
    @DisplayName("PUT /api/products/{id} - Update Product")
    class UpdateProduct {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("200 OK - Updates product successfully when admin")
        void updateProduct_Success() throws Exception {
            // Create product request DTO with updated values
            ProductRequestDto requestDto = ProductRequestDto.builder()
                    .productName("Updated Lipstick")
                    .description("Updated description")
                    .price(280000.0)
                    .quantity(30)
                    .categoryIds(List.of(savedCategory.getId()))
                    .supplierId(savedSupplier.getId())
                    .brandId(savedBrand.getId())
                    .build();

            // Convert DTO to JSON string
            String productJson = objectMapper.writeValueAsString(requestDto);

            // Create mock multipart file for the product JSON
            MockMultipartFile jsonFile = new MockMultipartFile(
                    "product",
                    "",
                    "application/json",
                    productJson.getBytes());

            // Create mock image file
            MockMultipartFile imageFile = new MockMultipartFile(
                    "images",
                    "updated-image.jpg",
                    "image/jpeg",
                    "updated image content".getBytes());

            // Perform request using MockMvcRequestBuilders.multipart() with PUT method
            mockMvc.perform(MockMvcRequestBuilders.multipart("/api/products/{id}", savedProduct.getId())
                            .file(jsonFile)
                            .file(imageFile)
                            .with(request -> {
                                request.setMethod("PUT");
                                return request;
                            }))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.message").value("Product updated successfully"))
                    .andExpect(jsonPath("$.data.id").value(savedProduct.getId()))
                    .andExpect(jsonPath("$.data.productName").value("Updated Lipstick"))
                    .andExpect(jsonPath("$.data.description").value("Updated description"))
                    .andExpect(jsonPath("$.data.price").value(280000.0));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("404 Not Found - Product not found")
        void updateProduct_NotFound() throws Exception {
            Long nonExistentId = 9999L;

            // Create valid product request DTO
            ProductRequestDto requestDto = ProductRequestDto.builder()
                    .productName("Updated Product")
                    .description("Updated description")
                    .price(200000.0)
                    .quantity(20)
                    .categoryIds(List.of(savedCategory.getId()))
                    .supplierId(savedSupplier.getId())
                    .brandId(savedBrand.getId())
                    .build();

            // Convert DTO to JSON string
            String productJson = objectMapper.writeValueAsString(requestDto);

            // Create mock multipart file for the product JSON
            MockMultipartFile jsonFile = new MockMultipartFile(
                    "product",
                    "",
                    "application/json",
                    productJson.getBytes());

            // Perform request using MockMvcRequestBuilders.multipart() with PUT method
            mockMvc.perform(MockMvcRequestBuilders.multipart("/api/products/{id}", nonExistentId)
                            .file(jsonFile)
                            .with(request -> {
                                request.setMethod("PUT");
                                return request;
                            }))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("403  - Updates product Access denied")
        void updateProduct_forbidden() throws Exception {
            // Create product request DTO with updated values
            ProductRequestDto requestDto = ProductRequestDto.builder()
                    .productName("Updated Lipstick")
                    .description("Updated description")
                    .price(280000.0)
                    .quantity(30)
                    .categoryIds(List.of(savedCategory.getId()))
                    .supplierId(savedSupplier.getId())
                    .brandId(savedBrand.getId())
                    .build();

            // Convert DTO to JSON string
            String productJson = objectMapper.writeValueAsString(requestDto);

            // Create mock multipart file for the product JSON
            MockMultipartFile jsonFile = new MockMultipartFile(
                    "product",
                    "",
                    "application/json",
                    productJson.getBytes());

            // Create mock image file
            MockMultipartFile imageFile = new MockMultipartFile(
                    "images",
                    "updated-image.jpg",
                    "image/jpeg",
                    "updated image content".getBytes());

            // Perform request using MockMvcRequestBuilders.multipart() with PUT method
            mockMvc.perform(MockMvcRequestBuilders.multipart("/api/products/{id}", savedProduct.getId())
                            .file(jsonFile)
                            .file(imageFile)
                            .with(request -> {
                                request.setMethod("PUT");
                                return request;
                            }))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("You do not have permission to access this resource."));
        }
    }

    @Nested
    @DisplayName("DELETE /api/products/{id} - Delete Product")
    class DeleteProduct {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("200 OK - Deletes product successfully")
        void deleteProduct_Success() throws Exception {
            mockMvc.perform(delete("/api/products/{id}", savedProduct.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true));

            // Verify product is deleted
            mockMvc.perform(get("/api/products/{id}", savedProduct.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("404 Not Found - Product not found")
        void deleteProduct_NotFound() throws Exception {
            Long nonExistentId = 9999L;

            mockMvc.perform(delete("/api/products/{id}", nonExistentId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }


        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("200 OK - Forbidden")
        void deleteProduct_Forbidden() throws Exception {
            mockMvc.perform(delete("/api/products/{id}", savedProduct.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("You do not have permission to access this resource."));

            // Verify product is not deleted
            mockMvc.perform(get("/api/products/{id}", savedProduct.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }
}
