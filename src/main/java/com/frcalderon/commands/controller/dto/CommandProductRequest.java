package com.frcalderon.commands.controller.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommandProductRequest {

    @NotNull
    private Long productId;

    @NotNull
    @Size(min = 1, max = 100)
    private String productName;

    @NotNull
    private Integer quantity;

    @NotNull
    @Digits(integer = 10, fraction = 2)
    private Double price;
}
