package com.olx.boilerplate.controller.dto.order.request;

import com.olx.boilerplate.usecase.order.command.CreateOrderCommand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    @NotBlank
    private String product;

    @Min(1)
    private int quantity;

    @Min(0)
    private double price;

    public CreateOrderCommand toCommand() {
        return new CreateOrderCommand(product, quantity, price);
    }
}
