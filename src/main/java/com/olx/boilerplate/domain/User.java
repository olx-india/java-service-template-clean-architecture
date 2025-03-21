package com.olx.boilerplate.domain;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class User {
    private Long id;
    private String name;
    private String email;

    public static User createUser(String name, String email) {
        return User.builder()
                .name(name)
                .email(email)
                .build();
    }
}
