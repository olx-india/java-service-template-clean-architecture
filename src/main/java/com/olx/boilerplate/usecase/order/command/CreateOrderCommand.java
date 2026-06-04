package com.olx.boilerplate.usecase.order.command;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderCommand {

    @NotBlank
    private String product;

    @Min(1)
    private int quantity;

    @Min(0)
    private double price;
}
