package com.olx.boilerplate.ut.usecase;

import com.olx.boilerplate.domain.User;
import com.olx.boilerplate.domain.exception.ResourceNotFoundException;
import com.olx.boilerplate.domain.repository.UserRepository;
import com.olx.boilerplate.usecase.users.DeleteUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User(userId, "a", "b@x.com")));
        doNothing().when(userRepository).delete(userId);

        deleteUser.execute(userId);

        verify(userRepository, times(1)).delete(userId);
    }

    @Test
    void testExecute_ShouldThrowWhenMissing() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> deleteUser.execute(2L));
    }
}
