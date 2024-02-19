package org.dockit.dockitserver.authentication.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dockit.dockitserver.authentication.services.APIKeyAuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

/**
 * Authentication Filter used for API key authentication
 */
public class APIKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(APIKeyAuthenticationFilter.class);

    private final Set<String> excludedUrls = Set.of("/api/admin/**", "/api/authenticate/**", "/actuator/**",
            "/api/audit/**", "/api/agent/**", "/api/apiKey/**");

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return excludedUrls.stream().anyMatch(url -> {
            AntPathRequestMatcher matcher = new AntPathRequestMatcher(url);
            return matcher.matches(request);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            Authentication authentication = APIKeyAuthenticationService
                    .getAuthentication(request);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            logger.debug("Could not authenticate using the APIKey!, \n {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
