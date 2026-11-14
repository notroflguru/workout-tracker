package notroflguru.workouttracker.config;

import notroflguru.workouttracker.user.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Отключаем CSRF
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // Эндпоинты для логина и регистрации доступны всем
                        .requestMatchers("/api/user/**").hasAnyRole("ADMIN", "USER") // Свой пользователь только авторизованным
                        .requestMatchers("/api/admin/**").hasRole("ADMIN") // Админские эндпоинты только админам
                        .requestMatchers(
                                "/api/swagger-ui/**", "/v3/api-docs/**", "/api/docs/**").permitAll() // Эндпоинты с API документацией доступны всем
                        .requestMatchers(HttpMethod.GET, "/api/exercises/**").permitAll() // GET /api/exercise можно всем желающим
                        .requestMatchers("/api/workouts/**").hasAnyRole("ADMIN", "USER") // /api/workouts можно только авторизованным
                        .requestMatchers(HttpMethod.POST, "/api/exercises/**").hasAnyRole("ADMIN", "USER") // POST /api/exercise можно только авторизованным
                        .anyRequest().authenticated()
                )
                // Добавляем JWT фильтр перед стандартным фильтром аутентификации
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Проверка логина и пароля в контроллере авторизации
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}