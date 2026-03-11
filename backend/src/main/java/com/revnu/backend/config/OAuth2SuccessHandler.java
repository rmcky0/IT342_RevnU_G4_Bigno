package com.revnu.backend.config;

import com.revnu.backend.dto.AuthResponse;
import com.revnu.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public OAuth2SuccessHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        Object principal = authentication.getPrincipal();

        if (!(principal instanceof OAuth2User oAuth2User)) {
            response.sendRedirect(frontendUrl + "/auth/callback?error=oauth2_principal_invalid");
            return;
        }

        try {
            AuthResponse authResponse = authService.authenticateWithGoogleOAuth2User(oAuth2User);
            String encodedToken = URLEncoder.encode(authResponse.getAccessToken(), StandardCharsets.UTF_8);
            String encodedEmail = URLEncoder.encode(authResponse.getEmail(), StandardCharsets.UTF_8);
            String encodedName  = URLEncoder.encode(authResponse.getFullName(), StandardCharsets.UTF_8);
            String encodedRole  = URLEncoder.encode(authResponse.getRole().name(), StandardCharsets.UTF_8);
            response.sendRedirect(frontendUrl
                    + "/auth/callback?token=" + encodedToken
                    + "&email=" + encodedEmail
                    + "&name=" + encodedName
                    + "&role=" + encodedRole);
        } catch (IllegalArgumentException ex) {
            if ("existing_account_requires_link".equals(ex.getMessage())) {
                // Send the user to the link page with their Google identity pre-filled
                String email    = oAuth2User.getAttribute("email");
                String googleId = oAuth2User.getAttribute("sub");
                String encodedEmail    = URLEncoder.encode(email    != null ? email    : "", StandardCharsets.UTF_8);
                String encodedGoogleId = URLEncoder.encode(googleId != null ? googleId : "", StandardCharsets.UTF_8);
                response.sendRedirect(frontendUrl
                        + "/auth/link-google?email=" + encodedEmail
                        + "&googleId=" + encodedGoogleId);
            } else {
                String encodedMessage = URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
                response.sendRedirect(frontendUrl + "/auth/callback?error=" + encodedMessage);
            }
        }
    }
}
