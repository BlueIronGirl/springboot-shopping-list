package de.shoppinglist.service;

import de.shoppinglist.dto.LoginDto;
import de.shoppinglist.dto.RegisterDto;
import de.shoppinglist.entity.User;
import de.shoppinglist.repository.ConfirmationTokenRepository;
import de.shoppinglist.repository.RoleRepository;
import de.shoppinglist.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.CharBuffer;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author alice_b
 */
@ExtendWith(MockitoExtension.class)
class UserAuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ConfirmationTokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserAuthenticationService userAuthenticationService;

    public static final String USERNAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String NAME = "NAME";
    public static final String TOKEN = "TOKEN";

    private User user;

    @BeforeEach
    public void setup() {
        user = User.builder()
                .id(1L)
                .username(USERNAME)
                .password(PASSWORD)
                .name(NAME)
                .token(TOKEN)
                .roles(new HashSet<>())
                .build();
    }

  /*
  FindByLogin
   */

    @Test
    void findByLogin_givenExistingUser_thenReturnUser() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));

        User result = userAuthenticationService.findByLogin(USERNAME);

        assertEquals(USERNAME, result.getUsername());
    }

    @Test
    void findByLogin_givenNotExistingUser_thenThrowException() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userAuthenticationService.findByLogin(USERNAME));
    }

  /*
  Login
   */

    @Test
    void login_givenExistingUser_thenReturnUser() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(Mockito.any(), Mockito.any())).thenReturn(true);

        LoginDto loginDto = new LoginDto("admin", "admin");
        User user = userAuthenticationService.login(loginDto);

        verify(passwordEncoder, Mockito.times(1)).matches(CharBuffer.wrap(loginDto.getPassword()), user.getPassword());
        assertEquals(USERNAME, user.getUsername());
    }

    @Test
    void login_givenNotExistingUser_thenThrowException() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());

        LoginDto loginDto = new LoginDto("admin", "admin");

        assertThrows(RuntimeException.class, () -> userAuthenticationService.login(loginDto));
    }

    @Test
    void login_givenExistingUserNoCorrectPassword_thenThrowException() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(Mockito.any(), Mockito.any())).thenReturn(false);

        LoginDto loginDto = new LoginDto("admin", "admin");

        assertThrows(RuntimeException.class, () -> userAuthenticationService.login(loginDto));
        verify(passwordEncoder, Mockito.times(1)).matches(CharBuffer.wrap(loginDto.getPassword()), user.getPassword());
    }

  /*
  Register
   */

    @Test
    void register_givenNotExistingUser_thenReturnUser() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        given(userRepository.saveAndFlush(Mockito.any(User.class))).willReturn(user);

        User user = userAuthenticationService.register(new RegisterDto("admin", "admin", "admin", "email@web.de"));

        verify(userRepository, Mockito.times(1)).saveAndFlush(Mockito.any(User.class));
        assertEquals(this.user.getUsername(), user.getUsername());
    }

    @Test
    void register_givenExisting_thenThrowException() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> userAuthenticationService.register(new RegisterDto("admin", "admin", "admin", "email@web.de")));
    }

}
