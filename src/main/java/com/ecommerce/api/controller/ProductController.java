package com.ecommerce.api.controller;

import com.ecommerce.api.dto.ProductDTO;
import com.ecommerce.api.mapper.ProductMapper;
import com.ecommerce.domain.product.ProductService;
import com.ecommerce.domain.product.ProductSearchService;
import com.ecommerce.search.product.ProductDocument;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductSearchService searchService;
    private final ProductMapper mapper;

    // 🧩 Criar produto — apenas ADMIN
    @PostMapping
    public ProductDTO create(@Valid @RequestBody ProductDTO dto) {
        return productService.create(dto);
    }

    // ✏️ Atualizar produto — apenas ADMIN
    @PutMapping("/{id}")
    public ProductDTO update(@PathVariable UUID id, @Valid @RequestBody ProductDTO dto) {
        return productService.update(id, dto);
    }

    // 🗑️ Deletar produto — apenas ADMIN
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        productService.delete(id);
    }

    // 🔍 Buscar produtos — aberta para todos
    @GetMapping
    public Page<ProductDTO> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return searchService.search(name, category, minPrice, maxPrice, page, size)
        .map(mapper::toDTO);



    }

    // 📦 Buscar produto por ID — aberta para todos
    @GetMapping("/{id}")
    public ProductDTO findById(@PathVariable UUID id) {
        return productService.findById(id);
    }
}
