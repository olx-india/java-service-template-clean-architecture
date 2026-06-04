package com.olx.boilerplate.ut.controller;

import com.olx.boilerplate.controller.UserController;
import com.olx.boilerplate.controller.dto.user.request.CreateUserRequest;
import com.olx.boilerplate.controller.dto.user.response.UserResponse;
import com.olx.boilerplate.domain.User;
import com.olx.boilerplate.usecase.users.CreateUser;
import com.olx.boilerplate.usecase.users.DeleteUser;
import com.olx.boilerplate.usecase.users.GetUser;
import com.olx.boilerplate.usecase.users.ListUsers;
import com.olx.boilerplate.usecase.users.UpdateUser;
import com.olx.boilerplate.usecase.users.command.CreateUserCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @Mock
    private CreateUser createUser;

    @Mock
    private GetUser getUser;

    @Mock
    private UpdateUser updateUser;

    @Mock
    private DeleteUser deleteUser;

    @Mock
    private ListUsers listUsers;

    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController(createUser, getUser, updateUser, deleteUser, listUsers);
    }

    @Test
    void createUser_ShouldReturnCreatedUser() {
        CreateUserRequest request = new CreateUserRequest("John Doe", "john@example.com");
        User mockUser = new User(1L, "John Doe", "john@example.com");

        when(createUser.execute(request.toCommand())).thenReturn(mockUser);

        ResponseEntity<UserResponse> response = userController.createUser(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockUser.getId(), response.getBody().getId());
        verify(createUser).execute(any(CreateUserCommand.class));
    }

    @Test
    void getUser_ShouldReturnUserDetails() {
        Long userId = 1L;
        User mockUser = new User(userId, "Jane Doe", "jane@example.com");

        when(getUser.execute(userId)).thenReturn(mockUser);

        ResponseEntity<UserResponse> response = userController.getUser(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockUser.getId(), response.getBody().getId());
        verify(getUser).execute(userId);
    }

    @Test
    void deleteUser_ShouldReturnNoContent() {
        Long userId = 1L;
        doNothing().when(deleteUser).execute(userId);

        ResponseEntity<Void> response = userController.deleteUser(userId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(deleteUser).execute(userId);
    }
}
