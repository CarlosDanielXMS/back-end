package com.example.back_end.auth;

import org.springframework.context.annotation.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class AuthBeans {

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  UserDetailsService userDetailsService(
      PasswordEncoder enc,
      @Value("${auth.default.email}") String email,
      @Value("${auth.default.password}") String senha) {
    UserDetails user = User.withUsername(email).password(enc.encode(senha)).roles("USER").build();
    return new InMemoryUserDetailsManager(user);
  }
}
