package com.olx.boilerplate.usecase.users.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserCommand {

    @NotNull
    private Long id;

    private String name;

    @Email
    private String email;
}
