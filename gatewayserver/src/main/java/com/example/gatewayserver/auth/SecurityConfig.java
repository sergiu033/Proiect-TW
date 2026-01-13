package com.example.gatewayserver.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    public SecurityWebFilterChain securityChain(ServerHttpSecurity http) {
        http
                .oauth2Login(oauth2 -> oauth2
                        .authenticationSuccessHandler(successHandler()))
                .oauth2Client(Customizer.withDefaults())
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/reviews/**").hasAnyRole("USER", "ADMIN")
                        .anyExchange().authenticated()
                );

        return http.build();
    }

    @Bean
    public ReactiveOAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        OidcReactiveOAuth2UserService handler = new OidcReactiveOAuth2UserService();

        return new ReactiveOAuth2UserService<OidcUserRequest, OidcUser>() {
            @Override
            public Mono<OidcUser> loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
                return handler.loadUser(userRequest)
                        .map(oidcUser -> {
                            Set<GrantedAuthority> authorities = new HashSet<>();

                            String email = oidcUser.getEmail();
                            if (email != null) {
                                if (email.endsWith("@bookreviews.com") ) {
                                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                                }
                                else
                                    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                            }

                            return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
                        });
            }
        };
    }

    @Bean
    public ServerAuthenticationSuccessHandler successHandler() {
        return (webFilterExchange, authentication) -> {
            webFilterExchange
                    .getExchange()
                    .getResponse()
                    .setStatusCode(HttpStatus.FOUND);

            webFilterExchange
                    .getExchange()
                    .getResponse()
                    .getHeaders()
                    .set("Location", "http://localhost:8072/home");

            return webFilterExchange.getExchange().getResponse().setComplete();
        };
    }
}
