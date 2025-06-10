package org.cosmetic.com.service;

import org.cosmetic.com.dto.request.ProductRequestDto;
import org.cosmetic.com.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> findAll();

    Optional<Product> findById(Long id);

    Product save(ProductRequestDto product,List<MultipartFile> images) throws IOException;

    void deleteById(Long id);

    Product update(Long id,ProductRequestDto product,List<MultipartFile> images) throws IOException;

    Page<Product> findAllProductNotInProductStatusDISCONTINUED(Pageable pageable);
}