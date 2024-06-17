package de.shoppinglist.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import de.shoppinglist.dto.LoginDto;
import de.shoppinglist.dto.RegisterDto;
import de.shoppinglist.entity.ConfirmationToken;
import de.shoppinglist.entity.Role;
import de.shoppinglist.entity.RoleName;
import de.shoppinglist.entity.User;
import de.shoppinglist.exception.EntityAlreadyExistsException;
import de.shoppinglist.exception.EntityNotFoundException;
import de.shoppinglist.exception.UnautorizedException;
import de.shoppinglist.repository.ConfirmationTokenRepository;
import de.shoppinglist.repository.RoleRepository;
import de.shoppinglist.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Component-Class providing the UserAuthenticationProvider to create and validate JWT-Tokens
 */
@Service
public class UserAuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(UserAuthenticationService.class);

    private String secretKey; // secret key for JWT
    private String clientUrl;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ConfirmationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Autowired
    public UserAuthenticationService(@Value("${security.jwt.token.secret-key:secret-key}") String secretKey, @Value("${client.url}") String clientUrl, UserRepository userRepository, RoleRepository roleRepository,
                                     ConfirmationTokenRepository tokenRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.secretKey = secretKey;
        this.clientUrl = clientUrl;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes()); // encode secret key
    }

    /**
     * Login a User to the Application and check the user and the password
     *
     * @param loginDto LoginDto containing the username and password
     * @return User-Object of the logged in user
     */
    public User login(LoginDto loginDto) {
        User user = findByLogin(loginDto.getUsername());
        user.setLastLoggedIn(LocalDateTime.now());
        userRepository.save(user);

        if (passwordEncoder.matches(CharBuffer.wrap(loginDto.getPassword()), user.getPassword())) {
            return user;
        }
        throw new UnautorizedException("Passwort falsch!");
    }

    /**
     * Register a new User to the Application
     *
     * @param registerDto RegisterDto containing the username, password and name of the new user
     * @return User-Object of the registered user
     */
    public User register(RegisterDto registerDto) {
        Optional<User> optionalUser = userRepository.findByUsername(registerDto.getUsername());

        if (optionalUser.isPresent()) {
            throw new EntityAlreadyExistsException("Benutzer existiert bereits");
        }

        Role guest = roleRepository.findByName(RoleName.ROLE_GUEST);
        HashSet<Role> roles = new HashSet<>();
        roles.add(guest);

        User user = User.builder()
                .username(registerDto.getUsername())
                .password(passwordEncoder.encode(CharBuffer.wrap(registerDto.getPassword())))
                .name(registerDto.getName())
                .email(registerDto.getEmail())
                .roles(roles)
                .createdAt(LocalDateTime.now())
                .build();

        user = userRepository.saveAndFlush(user);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .token(token)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();

        tokenRepository.save(confirmationToken);

        String link = this.clientUrl + "/registration-confirmation?token=" + token;

        emailService.sendEmail(user.getEmail(), "Shopping-List: Bitte bestätigen Sie ihre E-Mail-Adresse", "Bitte klicken Sie auf diesen Link, um Ihre E-Mail-Adresse zu bestätigen: " + link);

        return user;
    }

    public User findByLogin(String login) {
        return userRepository.findByUsername(login)
                .orElseThrow(() -> new EntityNotFoundException("Unbekannter User!"));
    }

    public User findCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User user) {
            String username = user.getUsername();
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("Unbekannter User!"));
        }
        return null;
    }

    /**
     * Method to create a JWT-Token for a given User that is valid for 12 hours
     *
     * @param user User to create the token for
     * @return JWT-Token
     */
    public String createToken(User user) {
        Date now = new Date();
        Date validUntil = new Date(now.getTime() + 43_200_000); // 12 Hours

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        return JWT.create()
                .withIssuer(user.getUsername())
                .withIssuedAt(now)
                .withExpiresAt(validUntil)
                .withClaim("name", user.getName())
                .sign(algorithm);
    }

    /**
     * Method to validate a given JWT-Token and return the Authentication for the User
     *
     * @param token JWT-Token to validate
     * @return Authentication for the User
     */
    public Authentication validateToken(HttpServletRequest request, String token) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        JWTVerifier verifier = JWT.require(algorithm).build();

        DecodedJWT decodedJWT = verifier.verify(token);

        User user = findByLogin(decodedJWT.getIssuer());

        return new UsernamePasswordAuthenticationToken(user, new WebAuthenticationDetailsSource().buildDetails(request), getAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Set<Role> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName().name()));
        }
        return authorities;
    }

    public String confirmEmailToken(String token) {
        ConfirmationToken confirmationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalStateException("Token not found or expired"));

        if (confirmationToken.getConfirmedAt() != null || confirmationToken.getUser().getRoles().stream().noneMatch(role -> role.getName() == RoleName.ROLE_GUEST)) {
            throw new IllegalStateException("Email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token expired");
        }

        confirmationToken.setConfirmedAt(LocalDateTime.now());
        tokenRepository.save(confirmationToken);

        User user = confirmationToken.getUser();
        Role roleUser = roleRepository.findByName(RoleName.ROLE_USER);
        HashSet<Role> roles = new HashSet<>();
        roles.add(roleUser);
        user.setRoles(roles);
        userRepository.save(user);

        return "Email confirmed successfully";
    }
}
