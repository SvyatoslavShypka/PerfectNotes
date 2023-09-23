package com.goit.finalproject.security;

import com.goit.finalproject.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {
    private final UserService userService;
    private final PasswordEncoderProvider passwordEncoder;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder.passwordEncoder());
        return provider;
    }

    @Autowired
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder.passwordEncoder());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(Customizer.withDefaults())
                .csrf(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers(
                                    "/note/list",
                                    "/note/create",
                                    "/note/edit/**"
                            )
//                            .fullyAuthenticated()
                            .authenticated()
                            .requestMatchers("/register", "/note/share/**").permitAll()
                            .requestMatchers("/users/**").hasAnyAuthority("ADMIN")
                            .requestMatchers("/user/addUser").hasAnyAuthority("ADMIN")
                            .requestMatchers("/user/edit/**").hasAnyAuthority("ADMIN")
                            .requestMatchers("/user/delete/**").hasAnyAuthority("ADMIN")
                            .anyRequest()
                            .authenticated();
                })
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .csrf(CsrfConfigurer::disable)
                .formLogin(login ->
                    login.loginPage("/login")
                            .defaultSuccessUrl("/note/list")
                            .permitAll()
                )
                .logout(LogoutConfigurer::permitAll)
                .build();
    }

}
