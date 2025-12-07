package com.example.gatewayserver.auth;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.cloudresourcemanager.CloudResourceManager;
import com.google.api.services.cloudresourcemanager.model.Binding;
import com.google.api.services.cloudresourcemanager.model.GetIamPolicyRequest;
import com.google.api.services.cloudresourcemanager.model.Policy;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
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

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class SecurityConfig {

    private static final String PROJECT_ID = "book-reviews-479506";

    @Bean
    public SecurityWebFilterChain securityChain(ServerHttpSecurity http) {
        http
                .oauth2Login(oauth2 -> oauth2
                        .authenticationSuccessHandler(successHandler()))
                .oauth2Client(Customizer.withDefaults())
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.GET, "/reviews/**").hasAnyRole("ADMIN", "USER", "MODERATOR")
                        .pathMatchers(HttpMethod.POST, "/reviews/**").hasAnyRole("ADMIN", "USER", "MODERATOR")
                        .pathMatchers(HttpMethod.PUT, "/reviews/**").hasAnyRole("ADMIN", "MODERATOR")
                        .pathMatchers(HttpMethod.DELETE, "/reviews/**").hasAnyRole("ADMIN")
                        .anyExchange().authenticated()
                );
         return http.build();
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

    @Bean
    public ReactiveOAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        OidcReactiveOAuth2UserService delegate = new OidcReactiveOAuth2UserService();

        return new ReactiveOAuth2UserService<OidcUserRequest, OidcUser>() {
            @Override
            public Mono<OidcUser> loadUser(OidcUserRequest userRequest) {
                return delegate.loadUser(userRequest)
                        .map(oidcUser -> {
                            Set<GrantedAuthority> mappedAuthorities = new HashSet<>(oidcUser.getAuthorities());
                            try {
                                Set<GrantedAuthority> iamRoles = getIamRoles(userRequest, oidcUser);
                                mappedAuthorities.addAll(iamRoles);
                            } catch (GeneralSecurityException | IOException e) {
                                System.out.println(e.getMessage());
                            }

                            System.out.println("Mapped authorities: " + mappedAuthorities);

                            return new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
                        });
            }
        };
    }

    private Set<GrantedAuthority> getIamRoles(OidcUserRequest userRequest, OidcUser oidcUser) throws GeneralSecurityException, IOException {
        String accessTokenValue = userRequest.getAccessToken().getTokenValue();
        AccessToken accessToken = new AccessToken(accessTokenValue, Date.from(userRequest.getAccessToken().getExpiresAt()));

        GoogleCredentials credentials = GoogleCredentials.create(accessToken);

        CloudResourceManager manager = new CloudResourceManager.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials)
        )
                .setApplicationName("GatewayServer")
                .build();

        GetIamPolicyRequest policyRequest = new GetIamPolicyRequest();
        Policy policy = manager.projects().getIamPolicy(PROJECT_ID, policyRequest).execute();

        String email = oidcUser.getEmail();
        String identifier = "user" + email;

        return policy.getBindings().stream()
                .filter(binding -> binding.getMembers() != null && binding.getMembers().contains(identifier))
                .map(Binding::getRole)
                .peek(role -> System.out.println("Role is:" + role))
                .map(this::mapIamRolesToApplicationRoles)
                .collect(Collectors.toSet());
    }

    private GrantedAuthority mapIamRolesToApplicationRoles(String role) {
        if ("roles/owner".equals(role))
            return new SimpleGrantedAuthority("ROLE_ADMIN");

        if ("roles/viewer".equals(role))
            return new SimpleGrantedAuthority("ROLE_USER");

        if ("roles/editor".equals(role))
            return new SimpleGrantedAuthority("ROLE_MODERATOR");

        return new SimpleGrantedAuthority("ROLE_USER");
    }
}
