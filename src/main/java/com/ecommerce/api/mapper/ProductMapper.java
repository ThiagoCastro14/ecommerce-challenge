package com.ecommerce.api.mapper;

import com.ecommerce.api.dto.ProductDTO;
import com.ecommerce.domain.product.Product;
import com.ecommerce.search.product.ProductDocument;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Component
public class ProductMapper {

    // Product → DTO
    public ProductDTO toDTO(Product entity) {
        if (entity == null) return null;
        return new ProductDTO(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getCategory(),
                entity.getPrice(),
                entity.getStockQuantity(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    // DTO → Product
    public Product toEntity(ProductDTO dto) {
        if (dto == null) return null;
        Product product = new Product();
        product.setId(dto.id());
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setCategory(dto.category());
        product.setPrice(dto.price());
        product.setStockQuantity(dto.stockQuantity());
        product.setCreatedAt(dto.createdAt());
        product.setUpdatedAt(dto.updatedAt());
        return product;
    }

    // Product → Document (MySQL → Elasticsearch)
    public ProductDocument toDocument(Product entity) {
        if (entity == null) return null;

        String id = entity.getId() != null ? entity.getId().toString() : null;

        LocalDateTime createdAt = entity.getCreatedAt() != null
                ? LocalDateTime.ofInstant(entity.getCreatedAt(), ZoneOffset.UTC)
                : null;

        LocalDateTime updatedAt = entity.getUpdatedAt() != null
                ? LocalDateTime.ofInstant(entity.getUpdatedAt(), ZoneOffset.UTC)
                : null;

        return ProductDocument.builder()
                .id(id)
                .name(entity.getName())
                .description(entity.getDescription())
                .category(entity.getCategory())
                .price(entity.getPrice())
                .stockQuantity(entity.getStockQuantity())
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    // ProductDocument → DTO (Elasticsearch → API)
    public ProductDTO toDTO(ProductDocument document) {
        if (document == null) return null;

        UUID uuid = null;
        if (document.getId() != null && !document.getId().isBlank()) {
            try {
                uuid = UUID.fromString(document.getId());
            } catch (IllegalArgumentException ignored) {
            }
        }

        Instant createdAt = document.getCreatedAt() != null
                ? document.getCreatedAt().toInstant(ZoneOffset.UTC)
                : null;

        Instant updatedAt = document.getUpdatedAt() != null
                ? document.getUpdatedAt().toInstant(ZoneOffset.UTC)
                : null;

        return new ProductDTO(
                uuid,
                document.getName(),
                document.getDescription(),
                document.getCategory(),
                document.getPrice(),
                document.getStockQuantity(),
                createdAt,
                updatedAt
        );
    }
}
