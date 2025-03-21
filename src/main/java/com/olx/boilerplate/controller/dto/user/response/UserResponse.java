package com.olx.boilerplate.controller.dto.user.response;

import com.olx.boilerplate.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse implements Serializable {

    private Long id;
    private String name;
    private String email;

    public static UserResponse buildFromEntity(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }
}
