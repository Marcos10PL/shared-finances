package pl.us.shared_finances.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.WeakKeyException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SECRET = "testowy-sekret-do-testow-co-najmniej-32-znaki";

    @Test
    void tokenZawieraIdUzytkownika() {
        // given
        JwtService jwtService = new JwtService(SECRET, 60);

        // when
        String token = jwtService.generateToken(5L);

        // then
        assertThat(jwtService.getUserId(token)).isEqualTo(5L);
    }

    @Test
    void zmienionyTokenJestOdrzucany() {
        JwtService jwtService = new JwtService(SECRET, 60);
        String token = jwtService.generateToken(5L);

        String[] parts = token.split("\\.");
        String tampered = parts[0] + ".f" + parts[1].substring(1) + "." + parts[2];

        assertThatThrownBy(() -> jwtService.getUserId(tampered))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void tokenPodpisanyInnymSekretemJestOdrzucany() {
        JwtService jwtService = new JwtService(SECRET, 60);
        JwtService other = new JwtService(SECRET + "-inny", 60);

        String foreignToken = other.generateToken(5L);

        assertThatThrownBy(() -> jwtService.getUserId(foreignToken))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void wygaslyTokenJestOdrzucany() {
        JwtService jwtService = new JwtService(SECRET, -1);
        String token = jwtService.generateToken(5L);

        assertThatThrownBy(() -> jwtService.getUserId(token))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void zaKrotkiSekretBlokujeStartAplikacji() {
        assertThatThrownBy(() -> new JwtService("za-krotki-sekret", 60))
                .isInstanceOf(WeakKeyException.class);
    }
}