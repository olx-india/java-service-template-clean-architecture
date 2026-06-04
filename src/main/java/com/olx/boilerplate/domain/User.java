package com.olx.boilerplate.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private String email;

    public static User createUser(String name, String email) {
        return User.builder()
                        .name(name)
                        .email(email)
                        .build();
    }

    public User update(String name, String email) {
        if (name != null) {
            this.name = name;
        }
        if (email != null) {
            this.email = email;
        }
        return this;
    }
}
