package com.example.project_economic.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    UserDetailsService userDetailsService;

    String[] PUBLIC_ENDPOINTS = {"/","/shop/**","/product-detail/**","/category","/faq","/about","/contact","/client/**","/isAuthenticated","/api/product-image/get/**", "/client/user/register-handle","/api/shoppingcarts/*", "/file/**"};
    String[] ADMIN_ENDPOINTS = {"/admin/**"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((registry) -> {registry
                    .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                    .requestMatchers(ADMIN_ENDPOINTS).permitAll()
                    .anyRequest().permitAll(); //Bat authenticate ngoai cac trang tren
                })
                .formLogin(httpSecurityFormLoginConfigurer -> {httpSecurityFormLoginConfigurer
                        .loginPage("/") //Trang chua bieu mau dang nhap
                        .loginProcessingUrl("/") //Trang xu ly login
                        .failureUrl("/") //Trang tra ve neu dang nhap that bai
                        .defaultSuccessUrl("/")
                        .permitAll();
                })
                .logout(httpSecurityLogoutConfigurer -> {httpSecurityLogoutConfigurer
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID") //xoa session
                    .clearAuthentication(true)
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/"); //Trang tra ve khi dang xuat thanh cong
                })
                .build();
    }

    //Providing password encode method
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        //Ngoai DaoAuthenticationProvider con co OAuth2Login AP, LDAP AP
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }
}
