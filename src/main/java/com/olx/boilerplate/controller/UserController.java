package com.olx.boilerplate.controller;

import com.olx.boilerplate.annotation.ReadOnlyTransaction;
import com.olx.boilerplate.annotation.ReadWriteTransaction;
import com.olx.boilerplate.controller.dto.user.request.CreateUserRequest;
import com.olx.boilerplate.controller.dto.user.response.UserResponse;
import com.olx.boilerplate.domain.User;
import com.olx.boilerplate.usercase.users.CreateUser;
import com.olx.boilerplate.usercase.users.DeleteUser;
import com.olx.boilerplate.usercase.users.GetUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final CreateUser createUser;
    private final GetUser getUser;
    private final DeleteUser deleteUser;

    public UserController(CreateUser createUser, GetUser getUser, DeleteUser deleteUser) {
        this.createUser = createUser;
        this.getUser = getUser;
        this.deleteUser = deleteUser;
    }

    @ReadWriteTransaction
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest createUserRequest) {
        User user = createUser.execute(createUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.buildFromEntity(user));
    }

    @ReadOnlyTransaction
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long userId) {
        User user = getUser.execute(userId);
        return ResponseEntity.ok(UserResponse.buildFromEntity(user));
    }

    @ReadWriteTransaction
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        deleteUser.execute(userId);
        return ResponseEntity.ok().build();
    }
}
