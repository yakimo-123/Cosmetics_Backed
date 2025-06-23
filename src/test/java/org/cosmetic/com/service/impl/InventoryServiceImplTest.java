package org.cosmetic.com.service.impl;

import org.cosmetic.com.exception.AppException;
import org.cosmetic.com.exception.ErrorCode;
import org.cosmetic.com.model.Inventory;
import org.cosmetic.com.model.Product;
import org.cosmetic.com.repository.InventoryRepository;
import org.cosmetic.com.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    @Nested
    @DisplayName("Find All Inventory Tests")
    class FindAllTests {

        @Test
        @DisplayName("Should return all inventories when they exist")
        void shouldReturnAllInventoriesWhenTheyExist() {
            // Given
            List<Inventory> expectedInventories = Arrays.asList(
                createInventory(1L, 10),
                createInventory(2L, 20)
            );
            when(inventoryRepository.findAll()).thenReturn(expectedInventories);

            // When
            List<Inventory> actualInventories = inventoryService.findAll();

            // Then
            assertNotNull(actualInventories);
            assertEquals(2, actualInventories.size());
            verify(inventoryRepository).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no inventories exist")
        void shouldReturnEmptyListWhenNoInventoriesExist() {
            // Given
            when(inventoryRepository.findAll()).thenReturn(List.of());

            // When
            List<Inventory> actualInventories = inventoryService.findAll();

            // Then
            assertNotNull(actualInventories);
            assertTrue(actualInventories.isEmpty());
            verify(inventoryRepository).findAll();
        }
    }

    @Nested
    @DisplayName("Find By Id Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return inventory when it exists")
        void shouldReturnInventoryWhenItExists() {
            // Given
            Long id = 1L;
            Inventory expectedInventory = createInventory(id, 10);
            when(inventoryRepository.findById(id)).thenReturn(Optional.of(expectedInventory));

            // When
            Optional<Inventory> actualInventory = inventoryService.findById(id);

            // Then
            assertTrue(actualInventory.isPresent());
            assertEquals(id, actualInventory.get().getId());
            verify(inventoryRepository).findById(id);
        }

        @Test
        @DisplayName("Should return empty when inventory doesn't exist")
        void shouldReturnEmptyWhenInventoryDoesntExist() {
            // Given
            Long id = 1L;
            when(inventoryRepository.findById(id)).thenReturn(Optional.empty());

            // When
            Optional<Inventory> actualInventory = inventoryService.findById(id);

            // Then
            assertFalse(actualInventory.isPresent());
            verify(inventoryRepository).findById(id);
        }
    }

    @Nested
    @DisplayName("Save Inventory Tests")
    class SaveTests {

        @Test
        @DisplayName("Should successfully save new inventory")
        void shouldSuccessfullySaveNewInventory() {
            // Given
            Inventory inventory = createInventory(1L, 10);
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

            // When
            Inventory savedInventory = inventoryService.save(inventory);

            // Then
            assertNotNull(savedInventory);
            assertEquals(inventory.getId(), savedInventory.getId());
            assertEquals(inventory.getQuantity(), savedInventory.getQuantity());
            verify(inventoryRepository).save(inventory);
        }
    }

    @Nested
    @DisplayName("Delete By Id Tests")
    class DeleteByIdTests {

        @Test
        @DisplayName("Should successfully delete existing inventory")
        void shouldSuccessfullyDeleteExistingInventory() {
            // Given
            Long id = 1L;
            when(inventoryRepository.existsById(id)).thenReturn(true);
            doNothing().when(inventoryRepository).deleteById(id);

            // When
            assertDoesNotThrow(() -> inventoryService.deleteById(id));

            // Then
            verify(inventoryRepository).existsById(id);
            verify(inventoryRepository).deleteById(id);
        }

        @Test
        @DisplayName("Should throw exception when trying to delete non-existent inventory")
        void shouldThrowExceptionWhenDeletingNonExistentInventory() {
            // Given
            Long id = 1L;
            when(inventoryRepository.existsById(id)).thenReturn(false);

            // When & Then
            AppException exception = assertThrows(AppException.class, 
                () -> inventoryService.deleteById(id));
            assertEquals(ErrorCode.INVENTORY_NOT_FOUND, exception.getErrorCode());
            verify(inventoryRepository).existsById(id);
            verify(inventoryRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("Get Or Create Inventory Tests")
    class GetOrCreateInventoryTests {

        @Test
        @DisplayName("Should update existing inventory when product exists")
        void shouldUpdateExistingInventoryWhenProductExists() {
            // Given
            Long productId = 1L;
            int quantity = 5;
            Inventory existingInventory = createInventory(1L, 10);
            when(inventoryRepository.findByProduct_Id(productId)).thenReturn(existingInventory);
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(existingInventory);

            // When
            Inventory result = inventoryService.getOrCreateInventory(productId, quantity);

            // Then
            assertNotNull(result);
            assertEquals(15, result.getQuantity()); // 10 + 5
            verify(inventoryRepository).findByProduct_Id(productId);
            verify(inventoryRepository).save(any(Inventory.class));
            verify(productRepository, never()).findById(any());
        }

        @Test
        @DisplayName("Should create new inventory when product exists but inventory doesn't")
        void shouldCreateNewInventoryWhenProductExistsButInventoryDoesnt() {
            // Given
            Long productId = 1L;
            int quantity = 5;
            Product product = new Product();
            product.setId(productId);
            Inventory newInventory = createInventory(1L, quantity);

            when(inventoryRepository.findByProduct_Id(productId)).thenReturn(null);
            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(newInventory);

            // When
            Inventory result = inventoryService.getOrCreateInventory(productId, quantity);

            // Then
            assertNotNull(result);
            assertEquals(quantity, result.getQuantity());
            verify(inventoryRepository).findByProduct_Id(productId);
            verify(productRepository).findById(productId);
            verify(inventoryRepository).save(any(Inventory.class));
        }

        @Test
        @DisplayName("Should throw exception when product ID is null")
        void shouldThrowExceptionWhenProductIdIsNull() {
            // Given
            Long productId = null;
            int quantity = 5;

            // When & Then
            AppException exception = assertThrows(AppException.class,
                () -> inventoryService.getOrCreateInventory(productId, quantity));
            assertEquals(ErrorCode.PRODUCT_ID_REQUIRED, exception.getErrorCode());
            verify(inventoryRepository, never()).findByProduct_Id(any());
            verify(productRepository, never()).findById(any());
        }

        @Test
        @DisplayName("Should throw exception when product doesn't exist")
        void shouldThrowExceptionWhenProductDoesntExist() {
            // Given
            Long productId = 1L;
            int quantity = 5;
            when(inventoryRepository.findByProduct_Id(productId)).thenReturn(null);
            when(productRepository.findById(productId)).thenReturn(Optional.empty());

            // When & Then
            AppException exception = assertThrows(AppException.class,
                () -> inventoryService.getOrCreateInventory(productId, quantity));
            assertEquals(ErrorCode.PRODUCT_NOT_FOUND, exception.getErrorCode());
            verify(inventoryRepository).findByProduct_Id(productId);
            verify(productRepository).findById(productId);
        }
    }

    private Inventory createInventory(Long id, int quantity) {
        Product product = new Product();
        product.setId(id);
        
        return Inventory.builder()
                .id(id)
                .product(product)
                .quantity(quantity)
                .build();
    }
}