//package org.cosmetic.com.service;
//
//    import org.cosmetic.com.dto.request.LoginRequestDto;
//    import org.cosmetic.com.dto.request.RegisterRequestDto;
//    import org.cosmetic.com.dto.response.LoginResponseDto;
//    import org.cosmetic.com.enums.Role;
//    import org.cosmetic.com.model.User;
//    import org.cosmetic.com.repository.UserRepository;
//    import org.cosmetic.com.security.jwt.JwtUtil;
//    import org.cosmetic.com.service.impl.AuthenticationServiceImpl;
//    import org.junit.jupiter.api.DisplayName;
//    import org.junit.jupiter.api.Test;
//    import org.junit.jupiter.api.extension.ExtendWith;
//    import org.mockito.InjectMocks;
//    import org.mockito.Mock;
//    import org.mockito.junit.jupiter.MockitoExtension;
//    import org.springframework.security.crypto.password.PasswordEncoder;
//
//    import java.util.Optional;
//
//    import static org.junit.jupiter.api.Assertions.*;
//    import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class AuthenticationServiceImplTest {
//
//        @Mock
//        private UserRepository userRepository;
//
//        @Mock
//        private PasswordEncoder passwordEncoder;
//
//        @Mock
//        private JwtUtil jwtUtil;
//
//        @InjectMocks
//        private AuthenticationServiceImpl authenticationService;
//
//        @Test
//        @DisplayName("Authenticate with valid credentials returns LoginResponseDto")
//        void authenticateWithValidCredentialsReturnsLoginResponseDto() {
//            LoginRequestDto request = LoginRequestDto.builder()
//                    .username("validUser")
//                    .password("validPassword")
//                    .build();
//            User user = new User();
//            user.setUsername("validUser");
//            user.setPassword("encodedPassword");
//
//            when(userRepository.findByUsername("validUser")).thenReturn(Optional.of(user));
//            when(passwordEncoder.matches("validPassword", "encodedPassword")).thenReturn(true);
//            when(jwtUtil.generateToken("validUser")).thenReturn("validToken");
//
//            LoginResponseDto response = authenticationService.authenticate(request);
//
//            assertEquals("validUser", response.getUsername());
//            assertEquals("validToken", response.getAccessToken());
//        }
//
//        @Test
//        @DisplayName("Authenticate with invalid username throws RuntimeException")
//        void authenticateWithInvalidUsernameThrowsRuntimeException() {
//            LoginRequestDto request = LoginRequestDto.builder()
//                    .username("invalidUser")
//                    .password("password")
//                    .build();
//
//            when(userRepository.findByUsername("invalidUser")).thenReturn(Optional.empty());
//
//            assertThrows(RuntimeException.class, () -> authenticationService.authenticate(request));
//        }
//
//        @Test
//        @DisplayName("Authenticate with invalid password throws RuntimeException")
//        void authenticateWithInvalidPasswordThrowsRuntimeException() {
//            LoginRequestDto request = LoginRequestDto.builder()
//                    .username("validUser")
//                    .password("invalidPassword")
//                    .build();
//            User user = new User();
//            user.setUsername("validUser");
//            user.setPassword("encodedPassword");
//
//            when(userRepository.findByUsername("validUser")).thenReturn(Optional.of(user));
//            when(passwordEncoder.matches("invalidPassword", "encodedPassword")).thenReturn(false);
//
//            assertThrows(RuntimeException.class, () -> authenticationService.authenticate(request));
//        }
//
//        @Test
//        @DisplayName("Register with new username saves user")
//        void registerWithNewUsernameSavesUser() {
//            RegisterRequestDto request = RegisterRequestDto.builder()
//                    .username("newUser")
//                    .password("password")
//                    .build();
//            User user = new User();
//            user.setUsername("newUser");
//            user.setPassword("encodedPassword");
//            user.setRole(Role.USER);
//
//            when(userRepository.existsByUsername("newUser")).thenReturn(false);
//            when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
//
//            authenticationService.register(request);
//
//            verify(userRepository, times(1)).save(argThat(savedUser ->
//                    savedUser.getUsername().equals("newUser") &&
//                    savedUser.getPassword().equals("encodedPassword") &&
//                    savedUser.getRole() == Role.USER));
//        }
//
//        @Test
//        @DisplayName("Register with existing username throws RuntimeException")
//        void registerWithExistingUsernameThrowsRuntimeException() {
//            RegisterRequestDto request = RegisterRequestDto.builder()
//                    .username("existingUser")
//                    .password("password")
//                    .build();
//
//            when(userRepository.existsByUsername("existingUser")).thenReturn(true);
//
//            assertThrows(RuntimeException.class, () -> authenticationService.register(request));
//        }
//    }