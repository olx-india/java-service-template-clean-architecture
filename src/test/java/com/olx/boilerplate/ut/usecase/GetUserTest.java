package com.olx.boilerplate.ut.usecase;

import com.olx.boilerplate.domain.User;
import com.olx.boilerplate.domain.exception.ResourceNotFoundException;
import com.olx.boilerplate.domain.repository.UserRepository;
import com.olx.boilerplate.usecase.users.GetUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        Long userId = 1L;
        User user = new User(userId, "JohnDoe", "test@email.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = getUser.execute(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testExecute_ShouldThrowWhenMissing() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> getUser.execute(2L));
    }
}
