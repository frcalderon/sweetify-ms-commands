package com.frcalderon.commands.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
public class UpdateStock {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    @NotNull
    private String request;

    @Column
    @NotNull
    private String uri;

    @Column
    @NotNull
    private boolean sent;
}
