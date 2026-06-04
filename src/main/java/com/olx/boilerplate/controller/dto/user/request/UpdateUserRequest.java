package com.olx.boilerplate.controller.dto.user.request;

import com.olx.boilerplate.usecase.users.command.UpdateUserCommand;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateUserRequest {

    @Email
    private String email;

    private String name;

    public UpdateUserCommand toCommand(Long id) {
        return new UpdateUserCommand(id, name, email);
    }
}
