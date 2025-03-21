package com.olx.boilerplate.ut.usercase;

import com.olx.boilerplate.domain.User;
import com.olx.boilerplate.domain.repository.UserRepository;
import com.olx.boilerplate.usercase.users.GetUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetUserTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GetUser getUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecute_ShouldReturnUser() {
        // Arrange
        Long userId = 1L;
        User user = new User(userId, "JohnDoe", "test@email.com");
        when(userRepository.getById(userId)).thenReturn(user);

        // Act
        User result = getUser.execute(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("JohnDoe", result.getName());
        verify(userRepository, times(1)).getById(userId);
    }
}
