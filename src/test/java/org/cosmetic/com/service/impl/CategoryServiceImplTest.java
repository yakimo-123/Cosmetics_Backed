package org.cosmetic.com.service.impl;

import org.cosmetic.com.dto.request.CategoryRequestDto;
import org.cosmetic.com.dto.response.CategoryResponseDto;
import org.cosmetic.com.exception.AppException;
import org.cosmetic.com.exception.ErrorCode;
import org.cosmetic.com.mapper.CategoryMapper;
import org.cosmetic.com.model.Category;
import org.cosmetic.com.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;

    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryServiceImpl(categoryRepository, categoryMapper);
    }

    // Helper method to create Category instances for testing
    private Category createCategory(Long id, String name) {
        Category category = new Category();
        category.setId(id);
        category.setCategoryName(name);
        return category;
    }

    @Nested
    @DisplayName("Find All Categories Tests")
    class FindAllTests {

        @Test
        @DisplayName("Should return all categories")
        void shouldReturnAllCategoriesAsDto() {
            List<Category> categories = Arrays.asList(
                    Category.builder().id(1L).categoryName("C1").build(),
                    Category.builder().id(2L).categoryName("C2").build()
            );
            when(categoryRepository.findAll()).thenReturn(categories);
            when(categoryMapper.toResponseDto(any())).thenAnswer(invocation -> {
                Category c = invocation.getArgument(0);
                return CategoryResponseDto.builder()
                        .id(c.getId())
                        .categoryName(c.getCategoryName())
                        .build();
            });

            List<CategoryResponseDto> result = categoryService.findAll();

            assertEquals(2, result.size());
            assertEquals("C1", result.get(0).getCategoryName());
            verify(categoryMapper, times(2)).toResponseDto(any());
        }

        @Test
        @DisplayName("Should return empty list when no categories exist")
        void shouldReturnEmptyListWhenNoCategoriesExist() {
            // Given
            when(categoryRepository.findAll()).thenReturn(List.of());

            // When
            List<CategoryResponseDto> result = categoryService.findAll();

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Find Category By Id Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return category when found")
        void shouldReturnCategoryResponseDtoWhenFound() {
            // Given
            Long categoryId = 1L;
            Category category = createCategory(categoryId, "Test Category");
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(categoryMapper.toResponseDto(category)).thenReturn(
                    CategoryResponseDto.builder().id(1L).categoryName("Test Category").build()
            );

            // When
            CategoryResponseDto result = categoryService.findById(categoryId);

            // Then
            assertNotNull(result);
            assertEquals(categoryId, result.getId());
            assertEquals("Test Category", result.getCategoryName());
        }

        @Test
        @DisplayName("Should throw exception when category not found")
        void shouldThrowExceptionWhenCategoryNotFound() {
            // Given
            Long categoryId = 1L;
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            // When & Then
            AppException ex = assertThrows(AppException.class, () -> categoryService.findById(categoryId));
            assertEquals(ErrorCode.CATEGORY_NOT_FOUND, ex.getErrorCode());
        }
    }

    @Nested
    @DisplayName("Save Category Tests")
    class SaveCategoryTests {

        @Test
        @DisplayName("Should successfully save new category")
        void shouldSaveNewCategoryResponseDto() {
            // Given
            CategoryRequestDto requestDto = new CategoryRequestDto();
            requestDto.setCategoryName("New Category");

            Category category = createCategory(1L, "New Category");
            CategoryResponseDto responseDto = CategoryResponseDto.builder()
                    .id(1L)
                    .categoryName("New Category")
                    .build();

            when(categoryMapper.toEntity(requestDto)).thenReturn(category);
            when(categoryRepository.save(any(Category.class))).thenReturn(category);
            when(categoryMapper.toResponseDto(category)).thenReturn(responseDto);

            // When
            CategoryResponseDto savedCategory = categoryService.save(requestDto);

            // Then
            assertNotNull(savedCategory);
            assertEquals("New Category", savedCategory.getCategoryName());
            assertEquals(1L, savedCategory.getId());

            verify(categoryRepository).save(category);
            verify(categoryMapper).toResponseDto(category);
        }

        @Test
        @DisplayName("Should throw exception when saving category with duplicate name")
        void shouldThrowExceptionWhenSavingDuplicateCategory() {
            // Given
            CategoryRequestDto requestDto = new CategoryRequestDto();
            requestDto.setCategoryName("Existing Category");

            when(categoryMapper.toEntity(requestDto)).thenReturn(new Category());
            when(categoryRepository.save(any(Category.class)))
                    .thenThrow(new AppException(ErrorCode.VALIDATION_FAILED));

            // When & Then
            assertThrows(AppException.class, () -> categoryService.save(requestDto));
        }
    }

    @Nested
    @DisplayName("Update Category Tests")
    class UpdateCategoryTests {

        @Test
        @DisplayName("Should successfully save new category")
        void shouldSaveNewCategoryResponseDto() {
            // Given
            CategoryRequestDto requestDto = new CategoryRequestDto();
            requestDto.setCategoryName("New Category");

            Category category = createCategory(1L, "New Category");
            CategoryResponseDto responseDto = CategoryResponseDto.builder()
                    .id(1L)
                    .categoryName("New Category")
                    .build();

            when(categoryMapper.toEntity(requestDto)).thenReturn(category);
            when(categoryRepository.save(category)).thenReturn(category);
            when(categoryMapper.toResponseDto(category)).thenReturn(responseDto);

            // When
            CategoryResponseDto savedCategory = categoryService.save(requestDto);

            // Then
            assertNotNull(savedCategory);
            assertEquals("New Category", savedCategory.getCategoryName());
            assertEquals(1L, savedCategory.getId());
            verify(categoryRepository).save(category);
            verify(categoryMapper).toResponseDto(category);
        }


        @Test
        @DisplayName("Should throw exception when updating non-existent category")
        void shouldThrowExceptionWhenUpdatingNonExistentCategory() {
            // Given
            Long categoryId = 1L;
            CategoryRequestDto requestDto = new CategoryRequestDto();

            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(AppException.class, () -> categoryService.update(categoryId, requestDto));
        }
    }

    @Nested
    @DisplayName("Delete Category Tests")
    class DeleteCategoryTests {

        @Test
        @DisplayName("Should successfully delete category")
        void shouldDeleteCategory() {
            // Given
            Long categoryId = 1L;
            when(categoryRepository.existsById(categoryId)).thenReturn(true);
            doNothing().when(categoryRepository).deleteById(categoryId);

            // When
            categoryService.deleteById(categoryId);

            // Then
            verify(categoryRepository).deleteById(categoryId);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent category")
        void shouldThrowExceptionWhenDeletingNonExistentCategory() {
            // Given
            Long categoryId = 1L;
            when(categoryRepository.existsById(categoryId)).thenReturn(false);

            // When & Then
            assertThrows(AppException.class, () -> categoryService.deleteById(categoryId));
        }
    }

    @Nested
    @DisplayName("Find Categories By Criteria Tests")
    class FindByCriteriaTests {

        @Test
        @DisplayName("Should return all categories")
        void shouldReturnAllCategories() {
            // Given
            List<Category> categories = Arrays.asList(
                    createCategory(1L, "Category 1"),
                    createCategory(2L, "Category 2")
            );
            when(categoryRepository.findAll()).thenReturn(categories);

            // When
            List<CategoryResponseDto> result = categoryService.findAll();

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(categoryRepository).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no categories exist")
        void shouldReturnEmptyListWhenNoCategoriesExist() {
            // Given
            when(categoryRepository.findAll()).thenReturn(List.of());

            // When
            List<CategoryResponseDto> result = categoryService.findAll();

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(categoryRepository).findAll();
        }
    }
}