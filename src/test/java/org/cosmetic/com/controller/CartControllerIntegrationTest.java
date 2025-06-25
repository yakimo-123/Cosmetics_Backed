package org.cosmetic.com.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cosmetic.com.enums.CartStatus;
import org.cosmetic.com.enums.Role;
import org.cosmetic.com.model.Cart;
import org.cosmetic.com.model.User;
import org.cosmetic.com.repository.CartRepository;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration-test (controller → service → JPA) for the /api/carts endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")          //  ← application-test.properties → H2 in MySQL-mode
@Transactional                   //  rollback after each test
@DisplayName("CartController integration tests")
class CartControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    User admin, alice;
    Cart aliceCart;

    /* ───────────────────────────────────────────────────── */

    @BeforeEach
    void setUp() {
        // ─── ADMIN ────────────────────────────────────────
        admin = userRepository.save(User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .email("admin@site.com")
                .role(Role.ADMIN)
                .enabled(true)
                .build());

        // ─── ALICE ────────────────────────────────────────
        alice = userRepository.save(User.builder()
                .username("alice")
                .password(passwordEncoder.encode("alice123"))
                .email("alice@site.com")
                .role(Role.USER)
                .enabled(true)
                .build());

        aliceCart = cartRepository.save(Cart.builder()
                .user(alice)
                .cartStatus(CartStatus.ACTIVE)
                .totalAmount(BigDecimal.ZERO)
                .build());
    }

    /* ====================================================
       1. GET /api/carts   (admin-only)
       ==================================================== */
    @Nested
    @DisplayName("GET  /api/carts  (list all)")
    class GetAll {

        @Test
        @DisplayName("200 OK – ADMIN sees all carts")
        void listAll_admin() throws Exception {
            mockMvc.perform(get("/api/carts")
                            .with(user("admin").roles("ADMIN")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.data.length()").value(1));
        }

        @Test
        @DisplayName("401 Unauthorized – anonymous user")
        void listAll_unauthenticated() throws Exception {
            mockMvc.perform(get("/api/carts"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("403 Forbidden – USER without ADMIN role")
        void listAll_user_forbidden() throws Exception {
            mockMvc.perform(get("/api/carts")
                            .with(user("alice").roles("USER")))
                    .andExpect(status().isForbidden());
        }
    }

    /* ====================================================
       2. GET /api/carts/{id}
       ==================================================== */
    @Nested
    @DisplayName("GET  /api/carts/{id}")
    class GetById {

        @Test
        @DisplayName("200 OK – owner USER retrieves own cart")
        void getById_owner() throws Exception {
            mockMvc.perform(get("/api/carts/{id}", aliceCart.getId())
                            .with(user("alice").roles("USER")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(true))
                    .andExpect(jsonPath("$.data.id").value(aliceCart.getId()));
        }

        @Test
        @DisplayName("200 OK – ADMIN retrieves arbitrary cart")
        void getById_admin() throws Exception {
            mockMvc.perform(get("/api/carts/{id}", aliceCart.getId())
                            .with(user("admin").roles("ADMIN")))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("401 Unauthorized – anonymous")
        void getById_unauthenticated() throws Exception {
            mockMvc.perform(get("/api/carts/{id}", aliceCart.getId()))
                    .andExpect(status().isUnauthorized());
        }
    }

    /* ====================================================
       3. POST /api/carts   (create / get active cart)
       ==================================================== */
    @Nested
    @DisplayName("POST /api/carts (create / get)")
    class CreateOrGet {

        @Test
        @DisplayName("201 Created – USER gets brand-new cart")
        void createCart_user() throws Exception {
            mockMvc.perform(post("/api/carts")
                            .with(user("alice").roles("USER"))
                            .param("userId", String.valueOf(alice.getId()))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").value(true));

        }

        @Test
        @DisplayName("401 Unauthorized – anonymous")
        void createCart_unauthenticated() throws Exception {
            mockMvc.perform(post("/api/carts"))
                    .andExpect(status().isUnauthorized());
        }
    }

    /* ====================================================
       4. DELETE /api/carts/{id}
       ==================================================== */
    @Nested
    @DisplayName("DELETE /api/carts/{id}")
    class DeleteCart {

        @Test
        @DisplayName("200 OK – ADMIN deletes any cart")
        void delete_admin() throws Exception {
            mockMvc.perform(delete("/api/carts/{id}", aliceCart.getId())
                            .with(user("admin").roles("ADMIN")))
                    .andExpect(status().isOk());

            assertThat(cartRepository.existsByIdAndCartStatus(aliceCart.getId(), CartStatus.ACTIVE)).isFalse();
        }

        @Test
        @DisplayName("200 OK – owner USER deletes own cart")
        void delete_owner() throws Exception {
            mockMvc.perform(delete("/api/carts/{id}", aliceCart.getId())
                            .with(user("alice").roles("USER")))
                    .andExpect(status().isOk());
            assertThat(cartRepository.existsByIdAndCartStatus(aliceCart.getId(), CartStatus.ACTIVE)).isFalse();
        }

        @Test
        @DisplayName("401 Unauthorized – anonymous")
        void delete_unauthenticated() throws Exception {
            mockMvc.perform(delete("/api/carts/{id}", aliceCart.getId()))
                    .andExpect(status().isUnauthorized());
            assertThat(cartRepository.existsById(aliceCart.getId())).isTrue();
        }
    }
}
