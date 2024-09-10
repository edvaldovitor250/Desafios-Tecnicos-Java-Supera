package br.com.edvaldo.todolist.serviceTests;


import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import br.com.edvaldo.todolist.domain.model.UserModel;
import br.com.edvaldo.todolist.domain.repository.IUserRepository;
import br.com.edvaldo.todolist.infra.service.AuthService;

public class AuthServiceTest {

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUserByUsername_Success() {
        String username = "testUser";
        UserModel userModel = new UserModel();
        UserDetails userDetails = userModel; 

        when(userRepository.findByLogin(username)).thenReturn(userDetails);

        UserDetails result = authService.loadUserByUsername(username);

        assertEquals(userDetails, result);
    }

    @Test
    void testLoadUserByUsername_UsernameNotFound() {
        String username = "nonExistentUser";

        when(userRepository.findByLogin(username)).thenReturn(null);

        UsernameNotFoundException thrownException = assertThrows(UsernameNotFoundException.class, () -> {
            authService.loadUserByUsername(username);
        });

        assertEquals("User not found with username: " + username, thrownException.getMessage());
    }
    
}
