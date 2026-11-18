package com.olx.boilerplate.ut.usecase;

import com.olx.boilerplate.domain.User;
import com.olx.boilerplate.domain.exception.ResourceNotFoundException;
import com.olx.boilerplate.domain.repository.UserRepository;
import com.olx.boilerplate.usecase.users.UpdateUser;
import com.olx.boilerplate.usecase.users.command.UpdateUserCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateUserTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UpdateUser updateUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecute_ShouldUpdateAndSaveUserWhenFound() {
        Long userId = 1L;
        User existing = new User(userId, "OldName", "old@email.com");
        UpdateUserCommand command = new UpdateUserCommand(userId, "NewName", "new@email.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        User result = updateUser.execute(command);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("NewName", result.getName());
        assertEquals("new@email.com", result.getEmail());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(existing);
    }

    @Test
    void testExecute_ShouldPreserveUnchangedFieldsWhenNullInCommand() {
        Long userId = 1L;
        User existing = new User(userId, "ExistingName", "existing@email.com");
        UpdateUserCommand command = new UpdateUserCommand(userId, null, "updated@email.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        User result = updateUser.execute(command);

        assertEquals("ExistingName", result.getName());
        assertEquals("updated@email.com", result.getEmail());
        verify(userRepository, times(1)).save(existing);
    }

    @Test
    void testExecute_ShouldThrowWhenUserMissingAndNeverSave() {
        Long userId = 99L;
        UpdateUserCommand command = new UpdateUserCommand(userId, "NewName", "new@email.com");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> updateUser.execute(command));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any());
    }
}
