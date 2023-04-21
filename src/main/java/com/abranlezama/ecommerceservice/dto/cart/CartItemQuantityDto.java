package com.abranlezama.ecommerceservice.dto.cart;

import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record CartItemQuantityDto(
        @Positive Long productId,
        @Positive Short quantity
) { }
