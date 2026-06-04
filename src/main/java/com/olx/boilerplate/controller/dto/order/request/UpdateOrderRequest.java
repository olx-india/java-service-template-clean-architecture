package com.olx.boilerplate.controller.dto.order.request;

import com.olx.boilerplate.usecase.order.command.UpdateOrderCommand;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateOrderRequest {

    private String product;

    @Min(1)
    private Integer quantity;

    @Min(0)
    private Double price;

    public UpdateOrderCommand toCommand(Long id) {
        return new UpdateOrderCommand(id, product, quantity, price);
    }
}
