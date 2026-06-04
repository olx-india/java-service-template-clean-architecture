package com.olx.boilerplate.controller;

import com.olx.boilerplate.annotation.ReadOnlyTransaction;
import com.olx.boilerplate.annotation.ReadWriteTransaction;
import com.olx.boilerplate.controller.dto.PageResponse;
import com.olx.boilerplate.controller.dto.user.request.CreateUserRequest;
import com.olx.boilerplate.controller.dto.user.request.UpdateUserRequest;
import com.olx.boilerplate.controller.dto.user.response.UserResponse;
import com.olx.boilerplate.domain.User;
import com.olx.boilerplate.usecase.users.CreateUser;
import com.olx.boilerplate.usecase.users.DeleteUser;
import com.olx.boilerplate.usecase.users.GetUser;
import com.olx.boilerplate.usecase.users.ListUsers;
import com.olx.boilerplate.usecase.users.UpdateUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Tag(name = "Users", description = "User management APIs")
public class UserController {

    private final CreateUser createUser;
    private final GetUser getUser;
    private final UpdateUser updateUser;
    private final DeleteUser deleteUser;
    private final ListUsers listUsers;

    public UserController(CreateUser createUser, GetUser getUser, UpdateUser updateUser, DeleteUser deleteUser,
                          ListUsers listUsers) {
        this.createUser = createUser;
        this.getUser = getUser;
        this.updateUser = updateUser;
        this.deleteUser = deleteUser;
        this.listUsers = listUsers;
    }

    @ReadWriteTransaction
    @PostMapping
    @Operation(summary = "Create a user")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        User user = createUser.execute(createUserRequest.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.buildFromEntity(user));
    }

    @ReadOnlyTransaction
    @GetMapping("/{userId}")
    @Operation(summary = "Get a user by ID")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long userId) {
        User user = getUser.execute(userId);
        return ResponseEntity.ok(UserResponse.buildFromEntity(user));
    }

    @ReadOnlyTransaction
    @GetMapping
    @Operation(summary = "List users with pagination")
    public ResponseEntity<PageResponse<UserResponse>> listUsers(Pageable pageable) {
        Page<User> users = listUsers.execute(pageable);
        return ResponseEntity.ok(new PageResponse<>(
                                                    users.getContent().stream().map(UserResponse::buildFromEntity).toList(),
                                                    users.getNumber(),
                                                    users.getSize(),
                                                    users.getTotalElements(),
                                                    users.getTotalPages()));
    }

    @ReadWriteTransaction
    @PutMapping("/{userId}")
    @Operation(summary = "Update a user")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long userId,
                                                   @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        User user = updateUser.execute(updateUserRequest.toCommand(userId));
        return ResponseEntity.ok(UserResponse.buildFromEntity(user));
    }

    @ReadWriteTransaction
    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        deleteUser.execute(userId);
        return ResponseEntity.noContent().build();
    }
}
