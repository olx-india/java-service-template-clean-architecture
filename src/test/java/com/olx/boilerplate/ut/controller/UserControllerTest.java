package com.olx.boilerplate.ut.controller;

import com.olx.boilerplate.controller.dto.user.request.CreateUserRequest;
import com.olx.boilerplate.controller.dto.user.response.UserResponse;
import com.olx.boilerplate.domain.User;
import com.olx.boilerplate.usercase.users.CreateUser;
import com.olx.boilerplate.usercase.users.DeleteUser;
import com.olx.boilerplate.usercase.users.GetUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.olx.boilerplate.controller.UserController;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private CreateUser createUser;

    @Mock
    private GetUser getUser;

    @Mock
    private DeleteUser deleteUser;

    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController(createUser, getUser, deleteUser);
    }

    @Test
    void createUser_ShouldReturnCreatedUser() {
        // Given
        CreateUserRequest request = new CreateUserRequest("John Doe", "john@example.com");
        User mockUser = new User(1L, "John Doe", "john@example.com");

        when(createUser.execute(request)).thenReturn(mockUser);

        // When
        ResponseEntity<UserResponse> response = userController.createUser(request);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockUser.getId(), response.getBody().getId());
        assertEquals(mockUser.getName(), response.getBody().getName());
        assertEquals(mockUser.getEmail(), response.getBody().getEmail());

        verify(createUser).execute(request);
    }

    @Test
    void getUser_ShouldReturnUserDetails() {
        // Given
        Long userId = 1L;
        User mockUser = new User(userId, "Jane Doe", "jane@example.com");

        when(getUser.execute(userId)).thenReturn(mockUser);

        // When
        ResponseEntity<UserResponse> response = userController.getUser(userId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockUser.getId(), response.getBody().getId());
        assertEquals(mockUser.getName(), response.getBody().getName());
        assertEquals(mockUser.getEmail(), response.getBody().getEmail());

        verify(getUser).execute(userId);
    }

    @Test
    void deleteUser_ShouldReturnOk() {
        // Given
        Long userId = 1L;
        doNothing().when(deleteUser).execute(userId);

        // When
        ResponseEntity<Void> response = userController.deleteUser(userId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());

        verify(deleteUser).execute(userId);
    }
}

