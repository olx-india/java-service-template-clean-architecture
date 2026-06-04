package com.olx.boilerplate.controller.dto.user.request;

import com.olx.boilerplate.usecase.users.command.CreateUserCommand;
import com.olx.boilerplate.usecase.users.command.UpdateUserCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    public CreateUserCommand toCommand() {
        return new CreateUserCommand(name, email);
    }
}
