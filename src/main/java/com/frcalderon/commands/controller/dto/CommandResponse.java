package com.frcalderon.commands.controller.dto;

import com.frcalderon.commands.model.Command;
import com.frcalderon.commands.model.CommandProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandResponse {

    public CommandResponse(Command command) {
        this.id = command.getId();
        this.deliveryDate = command.getDeliveryDate();
        this.status = command.getStatus();
        this.price = command.getPrice();
        this.products = command.getProducts();
    }

    private Long id;

    private LocalDate deliveryDate;

    private String status;

    private Double price;

    private List<CommandProduct> products;
}
