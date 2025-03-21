package com.olx.boilerplate.ut.usercase;

import com.olx.boilerplate.controller.dto.user.request.CreateUserRequest;
import com.olx.boilerplate.domain.User;
import com.olx.boilerplate.domain.repository.UserRepository;
import com.olx.boilerplate.usercase.users.CreateUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateUserTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CreateUser createUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecute_ShouldCreateUser() {
        // Arrange
        CreateUserRequest userRequest = new CreateUserRequest("JohnDoe", "test@email.com");
        User user = User.createUser(userRequest.name, userRequest.email);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = createUser.execute(userRequest);

        // Assert
        assertNotNull(result);
        assertEquals(userRequest.name, result.getName());
        assertEquals(userRequest.email, result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }
}
