package br.com.edvaldo.todolist.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import br.com.edvaldo.todolist.domain.controller.AuthController;
import br.com.edvaldo.todolist.domain.dto.token.TokenData;
import br.com.edvaldo.todolist.domain.dto.user.UserLogin;
import br.com.edvaldo.todolist.domain.dto.user.UserRegister;
import br.com.edvaldo.todolist.domain.model.UserModel;
import br.com.edvaldo.todolist.domain.repository.IUserRepository;
import br.com.edvaldo.todolist.infra.service.TokenService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void testRegisterSuccess() throws Exception {
        UserRegister userRegister = new UserRegister(null, null, null);
        UserModel userModel = new UserModel();
        userModel.setId(1L);

        when(passwordEncoder.encode(userRegister.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserModel.class))).thenReturn(userModel);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userRegister)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/auth/register/1"))
                .andExpect(jsonPath("$.login").value("username"));
    }

    @Test
    void testLoginSuccess() throws Exception {
        UserLogin userLogin = new UserLogin("username", "password");
        UserModel userModel = new UserModel();
        String token = "jwtToken";

        when(userRepository.findByLogin(userLogin.login())).thenReturn(userModel);
        when(passwordEncoder.matches(userLogin.password(), userModel.getPassword())).thenReturn(true);
        when(tokenService.gerarToken(userModel)).thenReturn(token);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token));
    }

    @Test
    void testLoginUnauthorized() throws Exception {
        UserLogin userLogin = new UserLogin("username", "password");
        UserModel userModel = new UserModel();

        when(userRepository.findByLogin(userLogin.login())).thenReturn(userModel);
        when(passwordEncoder.matches(userLogin.password(), userModel.getPassword())).thenReturn(false);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userLogin)))
                .andExpect(status().isUnauthorized());
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
