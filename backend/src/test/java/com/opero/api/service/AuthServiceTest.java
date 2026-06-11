package com.opero.api.service;

import com.opero.api.dto.AuthResponse;
import com.opero.api.dto.LoginRequest;
import com.opero.api.dto.RegisterRequest;
import com.opero.api.entity.Department;
import com.opero.api.entity.Role;
import com.opero.api.entity.User;
import com.opero.api.repository.DepartmentRepository;
import com.opero.api.repository.RoleRepository;
import com.opero.api.repository.UserRepository;
import com.opero.api.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private User mockUser;
    private Role mockRole;
    private Department mockDepartment;

    @BeforeEach
    void setUp() {
        mockRole = new Role();
        mockRole.setId(1L);
        mockRole.setName("STUDENT");

        mockDepartment = new Department();
        mockDepartment.setId(1L);
        mockDepartment.setName("Mantenimiento");

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("$2a$10$hashedPassword");
        mockUser.setFullName("Test User");
        mockUser.setRole(mockRole);
        mockUser.setDepartment(mockDepartment);
    }

    @Test
    void login_DeberiaRetornarAuthResponse_CuandoCredencialesSonCorrectas() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(anyString())).thenReturn("mock-jwt-token");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        assertNotNull(response.getUser());
        assertEquals("test@example.com", response.getUser().getEmail());
        assertEquals("Test User", response.getUser().getFullName());

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(passwordEncoder, times(1)).matches("password123", mockUser.getPassword());
        verify(jwtUtil, times(1)).generateToken("test@example.com");
    }

    @Test
    void login_DeberiaLanzarException_CuandoUsuarioNoExiste() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("noexiste@example.com");
        loginRequest.setPassword("password123");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.login(loginRequest));

        verify(userRepository, times(1)).findByEmail("noexiste@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    void login_DeberiaLanzarException_CuandoPasswordEsIncorrecta() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrongpassword");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authService.login(loginRequest));

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(passwordEncoder, times(1)).matches("wrongpassword", mockUser.getPassword());
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    void register_DeberiaCrearUsuario_CuandoDatosSonValidos() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("nuevo@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFullName("Nuevo Usuario");
        registerRequest.setRole("STUDENT");
        registerRequest.setDepartmentId(1L);

        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(mockRole));
        when(departmentRepository.findById(anyLong())).thenReturn(Optional.of(mockDepartment));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        var response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Test User", response.getFullName());

        verify(roleRepository, times(1)).findByName("STUDENT");
        verify(departmentRepository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_DeberiaLanzarException_CuandoRolNoExiste() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("nuevo@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFullName("Nuevo Usuario");
        registerRequest.setRole("ROL_INVALIDO");

        when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.register(registerRequest));

        verify(roleRepository, times(1)).findByName("ROL_INVALIDO");
        verify(userRepository, never()).save(any(User.class));
    }
}
