package notroflguru.workouttracker.user.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtCoreTest {

    private static final String SECRET =
            "a-long-random-secret-key-for-jwt-tests-2026";

    private JwtCore jwtCore;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtCore = new JwtCore(SECRET, 900_000L);
        userDetails = User.withUsername("pavel")
                .password("encoded-password")
                .roles("USER")
                .build();
    }

    @Test
    void generateToken_ReturnsTokenWithUsername() {
        String token = jwtCore.generateToken(userDetails);

        assertNotNull(token);
        assertEquals("pavel", jwtCore.extractUsername(token));
    }

    @Test
    void isTokenValid_ReturnsTrueForMatchingUserAndValidToken() {
        String token = jwtCore.generateToken(userDetails);

        assertTrue(jwtCore.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValid_ReturnsFalseForDifferentUser() {
        String token = jwtCore.generateToken(userDetails);
        UserDetails anotherUser = User.withUsername("another-user")
                .password("encoded-password")
                .roles("USER")
                .build();

        assertFalse(jwtCore.isTokenValid(token, anotherUser));
    }

    @Test
    void extractUsername_RejectsTokenSignedWithAnotherKey() {
        JwtCore anotherJwtCore = new JwtCore(
                "another-long-random-secret-key-for-jwt-tests-2026",
                900_000L
        );
        String token = anotherJwtCore.generateToken(userDetails);

        assertThrows(
                JwtException.class,
                () -> jwtCore.extractUsername(token)
        );
    }

    @Test
    void extractUsername_RejectsMalformedToken() {
        assertThrows(
                JwtException.class,
                () -> jwtCore.extractUsername("not-a-jwt")
        );
    }
}
