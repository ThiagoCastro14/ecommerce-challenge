package com.ecommerce.api.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductDTO(
        UUID id,

        @NotBlank(message = "O nome é obrigatório")
        String name,

        @NotBlank(message = "A descrição é obrigatória")
        String description,

        @NotBlank(message = "A categoria é obrigatória")
        String category,

        @NotNull @Positive
        BigDecimal price,

        @NotNull @Min(0)
        Integer stockQuantity,

        Instant createdAt,
        Instant updatedAt
) {}
