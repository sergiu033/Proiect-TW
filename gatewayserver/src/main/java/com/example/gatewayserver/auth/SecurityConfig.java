package com.example.gatewayserver.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http){

        http
                .oauth2Login(oauth2 -> oauth2
                        .authenticationSuccessHandler(successHandler()))
                .oauth2Client(Customizer.withDefaults())
                .authorizeExchange(exchange -> exchange
                        // Allow unauthenticated access for login/logout (handled by the app logic)
                        .pathMatchers(HttpMethod.POST, "/api/users/login", "/api/users/{id}/logout").permitAll()

                        // CRUD Operations - Restricted to LIBRARIANS
                        .pathMatchers(HttpMethod.POST, "/api/users").hasRole("LIBRARIAN") // createUser
                        .pathMatchers(HttpMethod.GET, "/api/users").hasRole("LIBRARIAN") // getAllUsers
                        .pathMatchers(HttpMethod.PUT, "/api/users/{id}").hasRole("LIBRARIAN") // updateUser
                        .pathMatchers(HttpMethod.DELETE, "/api/users/{id}").hasRole("LIBRARIAN") // deleteUser

                        // Specific User Access - PATRONS can view their own details; Staff can view all
                        .pathMatchers(HttpMethod.GET, "/api/users/{id}").hasAnyRole("LIBRARIAN", "PATRON", "ARCHIVIST") // getUserById
                        .pathMatchers(HttpMethod.POST, "/api/users/{id}/change-password").authenticated() // User must be logged in to change their own password

                        // Search and Filter Endpoints - Restricted to Staff (Librarians and Archivists)
                        .pathMatchers("/api/users/search", "/api/users/filter", "/api/users/sorted/**").hasAnyRole("LIBRARIAN", "ARCHIVIST")

                        // All other exchanges must be authenticated
                        .anyExchange().authenticated())
                .csrf(csrf -> csrf.disable()); // Cross-Site Request Forgery
        return http.build();
    }

    @Bean
    public ReactiveOAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        OidcReactiveOAuth2UserService delegate = new OidcReactiveOAuth2UserService();

        return new ReactiveOAuth2UserService<OidcUserRequest, OidcUser>() {
            @Override
            public Mono<OidcUser> loadUser(OidcUserRequest userRequest) {
                return delegate.loadUser(userRequest)
                        .map(oidcUser -> {
                            Set<GrantedAuthority> mappedAuthorities = new HashSet<>(oidcUser.getAuthorities());

                            String email = oidcUser.getEmail();
                            if (email != null) {
                                // Assigning library roles based on email with the mandatory "ROLE_" prefix
                                if (email.endsWith("@gmail.com"))
                                    mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_LIBRARIAN"));
                                else
                                    mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_PATRON"));
                            }

                            System.out.println("Mapped authorities: " + mappedAuthorities);

                            return new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
                        });
            }
        };
    }
    @Bean
    public ServerAuthenticationSuccessHandler successHandler() {
        return (webFilterExchange, authentication) -> {
            System.out.println("Authenticated authorities at success: " + authentication.getAuthorities());

            webFilterExchange
                    .getExchange()
                    .getResponse()
                    .setStatusCode(org.springframework.http.HttpStatus.FOUND);

            // Assuming this is still the desired application entry point
            webFilterExchange
                    .getExchange()
                    .getResponse()
                    .getHeaders().set("Location", "http://localhost:8072/airport"); // You might want to change this URL to a library-themed one!

            return webFilterExchange.getExchange().getResponse().setComplete();
        };
    }
}
