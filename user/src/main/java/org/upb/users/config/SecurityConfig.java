package org.upb.users.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Noua sintaxă Lambda DSL pentru dezactivarea CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // Configurarea autorizării (permitAll pentru tot, deoarece Gateway se ocupă de securitate)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )

                // Dezactivare form login și http basic folosind noua sintaxă
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}