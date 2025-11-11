package com.user.infrastructure.security;

import com.user.domain.auth.TokenData;
import com.user.domain.auth.port.TokenProviderPort;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProviderPort tokenProvider;

    public JwtAuthenticationFilter(TokenProviderPort tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {

        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }

        String token = auth.substring(7);
        try {
            if (!tokenProvider.isValid(token)) {
                throw new BadCredentialsException("Token inválido o expirado");
            }

            TokenData data = tokenProvider.parse(token);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            data.getUserId().toString(), null, List.of());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(req, res);

        } catch (BadCredentialsException | JwtException e) {
            SecurityContextHolder.clearContext();
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json");
            res.getWriter().write("{\"mensaje\":\"Token inválido o expirado\"}");
        }
    }
}
