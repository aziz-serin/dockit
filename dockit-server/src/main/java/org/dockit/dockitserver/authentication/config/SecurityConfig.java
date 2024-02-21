package org.dockit.dockitserver.authentication.config;

import org.dockit.dockitserver.authentication.filters.APIKeyAuthenticationFilter;
import org.dockit.dockitserver.authentication.filters.JwtAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Contains the configuration for REST-api security
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final AuthEntryPoint authEntryPoint;

    /**
     * @param authEntryPoint Injected dependency for error handling
     */
    public SecurityConfig(AuthEntryPoint authEntryPoint) {
        this.authEntryPoint = authEntryPoint;
    }


    /**
     * Bean to configure the http security details
     *
     * @param http HTTPSecurity object, {@link org.springframework.security.config.annotation.web.builders.HttpSecurity}
     * @return Built http configuration
     * @throws Exception Can throw an Exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/api/authenticate/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement((sessionManagement) -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new APIKeyAuthenticationFilter(), JwtAuthenticationFilter.class);

        // Handle exceptions
        http.exceptionHandling((exception) -> exception
                .authenticationEntryPoint(this.authEntryPoint));

        return http.build();
    }

    /**
     * Bean to set up the {@link org.dockit.dockitserver.authentication.filters.APIKeyAuthenticationFilter}
     *
     * @return filter registration bean
     */
    @Bean
    public FilterRegistrationBean<APIKeyAuthenticationFilter> apiKeyAuthenticationFilter() {
        FilterRegistrationBean<APIKeyAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new APIKeyAuthenticationFilter());
        registrationBean.addUrlPatterns("/api/write/**");
        return registrationBean;
    }

    /**
     * Bean to set up the {@link org.dockit.dockitserver.authentication.filters.JwtAuthenticationFilter}
     *
     * @return filter registration bean
     */
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilter() {
        FilterRegistrationBean<JwtAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JwtAuthenticationFilter());
        registrationBean.addUrlPatterns("/api/admin/**", "/api/audit/**", "/api/agent/**", "/api/apiKey/**",
                "/api/alert/**");
        return registrationBean;
    }
}
