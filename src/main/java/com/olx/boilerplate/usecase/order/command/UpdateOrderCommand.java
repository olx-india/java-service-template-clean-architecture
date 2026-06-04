package com.olx.boilerplate.usecase.order.command;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderCommand {

    @NotNull
    private Long id;

    private String product;

    @Min(1)
    private Integer quantity;

    @Min(0)
    private Double price;
}
