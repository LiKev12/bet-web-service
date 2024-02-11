// import lombok.RequiredArgsConstructor;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// @RequiredArgsConstructor
// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

//     private final UserAuthenticationEntryPoint userAuthenticationEntryPoint;
//     private final UserAuthProvider userAuthProvider;

//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         http
//             .exceptionHandling()
//             .authenticationEntryPoint(userAuthenticationEntryPoint)
//             .and()
//             .addFilterBefore(new JwtAuthFilter(userAuthProvider), BasicAuthenticationFilter.class)
//             .csrf().disable()
//             .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//             .and()
//             .authorizeHttpRequests((requests) -> requests
//                 .requestMatchers(HttpMethod.POST, "/login", "/register").permitAll().anyRequest().authenticated()
//             );
//         return http.build();


//     }
// }