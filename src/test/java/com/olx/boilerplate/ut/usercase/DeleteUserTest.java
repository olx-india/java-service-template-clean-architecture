package com.olx.boilerplate.ut.usercase;

import com.olx.boilerplate.domain.repository.UserRepository;
import com.olx.boilerplate.usercase.users.DeleteUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class DeleteUserTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DeleteUser deleteUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecute_ShouldDeleteUser() {
        // Arrange
        Long userId = 1L;
        doNothing().when(userRepository).delete(userId);

        // Act
        deleteUser.execute(userId);

        // Assert
        verify(userRepository, times(1)).delete(userId);
    }
}