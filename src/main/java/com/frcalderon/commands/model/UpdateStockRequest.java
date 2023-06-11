package com.frcalderon.commands.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateStockRequest {

    @NotBlank
    private Long productId;

    @NotNull
    private Integer stock;
}
