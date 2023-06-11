package com.frcalderon.commands.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandProduct {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "command_id")
    @JsonIgnore
    private Command command;

    @Column
    @NotNull
    private Long productId;

    @Column
    @NotNull
    private String productName;

    @Column
    @NotNull
    private Integer quantity;

    @Column
    @NotNull
    private Double unitPrice;

    @Column
    @NotNull
    private Double totalPrice;
}
