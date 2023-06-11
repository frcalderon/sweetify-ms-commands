package com.frcalderon.commands.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Command {

    public static final String TO_DO = "TO DO";
    public static final String IN_PROGRESS = "IN PROGRESS";
    public static final String PREPARED = "PREPARED";
    public static final String DELIVERED = "DELIVERED";
    public static final String CANCELLED = "CANCELLED";


    @Id
    @GeneratedValue
    private Long id;

    @Column
    @NotNull
    private LocalDate deliveryDate;

    @Column
    @NotNull
    private String status;

    @Column
    @NotNull
    private Double price;

    @OneToMany(mappedBy = "command")
    private List<CommandProduct> products;
}
