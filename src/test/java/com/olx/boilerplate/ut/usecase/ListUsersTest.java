package com.olx.boilerplate.ut.usecase;

import com.olx.boilerplate.domain.PageQuery;
import com.olx.boilerplate.domain.PageResult;
import com.olx.boilerplate.domain.User;
import com.olx.boilerplate.domain.repository.UserRepository;
import com.olx.boilerplate.usecase.users.ListUsers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ListUsersTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ListUsers listUsers;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecute_ShouldDelegatePageQueryAndReturnPageResultUnchanged() {
        PageQuery pageQuery = PageQuery.of(0, 10);
        List<User> users = List.of(
                                   new User(1L, "Alice", "alice@example.com"),
                                   new User(2L, "Bob", "bob@example.com"));
        PageResult<User> expected = PageResult.of(users, 0, 10, 2L);
        when(userRepository.findAll(pageQuery)).thenReturn(expected);

        PageResult<User> result = listUsers.execute(pageQuery);

        assertSame(expected, result);
        assertEquals(2, result.content().size());
        verify(userRepository, times(1)).findAll(pageQuery);
    }

    @Test
    void testExecute_ShouldReturnEmptyPageResultWhenNoUsersExist() {
        PageQuery pageQuery = PageQuery.of(1, 5);
        PageResult<User> expected = PageResult.of(List.of(), 1, 5, 0L);
        when(userRepository.findAll(pageQuery)).thenReturn(expected);

        PageResult<User> result = listUsers.execute(pageQuery);

        assertSame(expected, result);
        assertEquals(0, result.content().size());
        verify(userRepository, times(1)).findAll(pageQuery);
    }
}
