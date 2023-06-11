package com.frcalderon.commands.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

@Data
@Builder
public class CommandRequest {

    @NotNull
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private String deliveryDate;

    @NotNull
    private List<CommandProductRequest> commandProductList;
}
