package br.com.edvaldo.todolist.serviceTests;

import com.auth0.jwt.exceptions.JWTCreationException;

import br.com.edvaldo.todolist.domain.model.UserModel;
import br.com.edvaldo.todolist.infra.service.TokenService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

public class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tokenService = new TokenService();
        //tokenService.secret = "mySecretKey"; 
    }

    @Test
    void testGerarToken() {
        UserModel user = new UserModel(); 
        user.setLogin("testUser");

        String token = tokenService.gerarToken(user);

        assertNotNull(token);
        assertTrue(token.startsWith("eyJ"));
    }

    @Test
    void testGetSubject_ValidToken() {
        UserModel user = new UserModel(); 
        user.setLogin("testUser");
        String token = tokenService.gerarToken(user);

        String subject = tokenService.getSubject(token);

        assertEquals("testUser", subject);
    }

    @Test
    void testGetSubject_InvalidToken() {
        String invalidToken = "invalidToken";

        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            tokenService.getSubject(invalidToken);
        });

        assertEquals("Token JWT inválido ou expirado!", thrownException.getMessage());
    }

    @Test
    void testGerarToken_Exception() {
        TokenService faultyTokenService = new TokenService() {
            @Override
            public String gerarToken(UserModel usuario) {
                throw new JWTCreationException("Simulated exception", null);
            }
        };

        UserModel user = new UserModel(); 
        user.setLogin("testUser");

        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            faultyTokenService.gerarToken(user);
        });

        assertEquals("erro ao gerar token jwt", thrownException.getMessage());
    }
}
