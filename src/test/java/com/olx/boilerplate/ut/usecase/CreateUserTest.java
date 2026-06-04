package com.olx.boilerplate.ut.usecase;

import com.olx.boilerplate.domain.User;
import com.olx.boilerplate.domain.port.EventPublisher;
import com.olx.boilerplate.domain.repository.UserRepository;
import com.olx.boilerplate.usecase.users.CreateUser;
import com.olx.boilerplate.usecase.users.command.CreateUserCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateUserTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private CreateUser createUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecute_ShouldCreateUser() {
        CreateUserCommand command = new CreateUserCommand("JohnDoe", "test@email.com");
        User user = User.createUser(command.getName(), command.getEmail());
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = createUser.execute(command);

        assertNotNull(result);
        assertEquals(command.getName(), result.getName());
        assertEquals(command.getEmail(), result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
        verify(eventPublisher, times(1)).publishUserCreated(any());
    }
}
