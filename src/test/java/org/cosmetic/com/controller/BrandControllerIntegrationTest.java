package org.cosmetic.com.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cosmetic.com.dto.request.BrandRequestDto;
import org.cosmetic.com.model.Brand;
import org.cosmetic.com.repository.BrandRepository;
import org.cosmetic.com.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration-tests (controller → service → JPA) for the /api/brands endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")      // ← uses application-test.properties (H2 in MySQL mode)
@Transactional               // ← roll back after each @Test
@DisplayName("BrandController integration")
class BrandControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    BrandRepository brandRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;   // to create an ADMIN user if you test real auth

    /* -------------------------------------------------------------
       GET /api/brands
       ------------------------------------------------------------- */
    @Nested
    @DisplayName("GET /api/brands (all)")
    class GetAll {

        @BeforeEach
        void init() {
            brandRepository.saveAll(List.of(
                    Brand.builder().name("Nike").build(),
                    Brand.builder().name("Adidas").build()
            ));
        }

        @Test
        @DisplayName("200 OK – returns list of brands")
        void getAll_returnsList() throws Exception {

            mockMvc.perform(get("/api/brands"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.data.length()").value(2))
                    .andExpect(jsonPath("$.data[0].name").value("Nike"));
        }
    }

    /* -------------------------------------------------------------
       GET /api/brands/{id}
       ------------------------------------------------------------- */
    @Nested
    @DisplayName("GET /api/brands/{id}")
    class GetById {

        Long brandId;

        @BeforeEach
        void init() {
            brandId = brandRepository.save(Brand.builder().name("Puma").build()).getId();
        }

        @Test
        @DisplayName("200 OK – brand exists")
        void getById_found() throws Exception {
            mockMvc.perform(get("/api/brands/{id}", brandId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.data.name").value("Puma"));
        }

        @Test
        @DisplayName("404 Not-Found – brand missing")
        void getById_notFound() throws Exception {
            mockMvc.perform(get("/api/brands/{id}", 999L))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(false))
                    .andExpect(jsonPath("$.message").value("Brand not found"));
        }
    }

    /* -------------------------------------------------------------
       POST /api/brands   (ADMIN only)
       ------------------------------------------------------------- */
    @Nested
    @DisplayName("POST /api/brands")
    class CreateBrand {

        @Test
        @WithMockUser(username = "admin", roles = "ADMIN")   // ✨ bypass @PreAuthorize
        @DisplayName("201 Created – admin creates new brand")
        void create_ok() throws Exception {

            BrandRequestDto req = BrandRequestDto.builder()
                    .name("Reebok")
                    .build();

            mockMvc.perform(post("/api/brands")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.data.name").value("Reebok"));

            assertThat(brandRepository.existsByNameIgnoreCase("Reebok")).isTrue();
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("403 Forbidden – anonymous user")
        void create_forbidden() throws Exception {

            BrandRequestDto req = BrandRequestDto.builder().name("Forbidden").build();

            mockMvc.perform(post("/api/brands")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isForbidden());
            assertThat(brandRepository.existsByNameIgnoreCase("Forbidden")).isFalse();

        }
    }

    /* -------------------------------------------------------------
       PUT /api/brands/{id}   (ADMIN only)
       ------------------------------------------------------------- */
    @Nested
    @DisplayName("PUT /api/brands/{id}")
    class UpdateBrand {

        Long id;

        @BeforeEach
        void init() {
            id = brandRepository.save(Brand.builder().name("OldName").build()).getId();
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("200 OK – admin updates brand")
        void update_ok() throws Exception {
            BrandRequestDto req = BrandRequestDto.builder().name("NewName").build();

            mockMvc.perform(put("/api/brands/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.name").value("NewName"));

            assertThat(brandRepository.findById(id).get().getName()).isEqualTo("NewName");
        }
    }

    /* -------------------------------------------------------------
       DELETE /api/brands/{id}   (ADMIN only)
       ------------------------------------------------------------- */
    @Nested
    @DisplayName("DELETE /api/brands/{id}")
    class DeleteBrand {

        Long id;

        @BeforeEach
        void init() {
            id = brandRepository.save(Brand.builder().name("Temp").build()).getId();
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("200 OK – admin deletes brand")
        void delete_ok() throws Exception {

            mockMvc.perform(delete("/api/brands/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true));

            assertThat(brandRepository.existsById(id)).isFalse();
        }
    }
}
