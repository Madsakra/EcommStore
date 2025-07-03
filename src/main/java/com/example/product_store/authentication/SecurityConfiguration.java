package com.example.product_store.authentication;

import com.example.product_store.authentication.jwt.JwtAuthenticationFilter;
import com.example.product_store.authentication.securityHandler.CustomAccessDeniedHandler;
import com.example.product_store.authentication.securityHandler.CustomAuthenticationEntryPoint;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

  @Bean
  public AuthenticationManager authenticationManager(HttpSecurity httpSecurity)
      throws Exception {
    return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class).build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity)
      throws Exception {
    return httpSecurity
        .csrf(AbstractHttpConfigurer::disable) // Disables CSRF
        .authorizeHttpRequests(
            authorize -> {
              authorize.requestMatchers("/auth/**").permitAll();
              authorize
                  .requestMatchers("/superAdmin/**")
                  .hasAuthority("ROLE_SUPER_ADMIN");
              authorize.requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN");
              authorize.requestMatchers("/user/**").hasAuthority("ROLE_USER");
              authorize
                  .requestMatchers(
                      "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**")
                  .permitAll();
              authorize.anyRequest().authenticated();
            })
        .exceptionHandling(
            exception ->
                exception
                    .authenticationEntryPoint(customAuthenticationEntryPoint())
                    .accessDeniedHandler(customAccessDeniedHandler()))
        .logout(
            logout ->
                logout
                    .logoutUrl("/auth/logout")
                    .logoutSuccessHandler(
                        (request, response, authentication) -> {
                          response.setStatus(HttpServletResponse.SC_OK);
                          response.getWriter().write("Logout successful");
                        })
                    .invalidateHttpSession(true)
                    // SERVER CAN ONLY DELETE COOKIES, BUT IF CLIENT KEEPS IT, THEY STILL
                    // CAN ACCESS
                    .deleteCookies("JSESSIONID")
                    .permitAll() // Allow everyone to access logout
            )
        .addFilterBefore(
            jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  public AccessDeniedHandler customAccessDeniedHandler() {
    return new CustomAccessDeniedHandler();
  }

  public AuthenticationEntryPoint customAuthenticationEntryPoint() {
    return new CustomAuthenticationEntryPoint();
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter();
  }
}
