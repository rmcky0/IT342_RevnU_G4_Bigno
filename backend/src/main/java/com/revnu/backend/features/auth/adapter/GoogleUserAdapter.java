package com.revnu.backend.features.auth.adapter;

import org.springframework.security.oauth2.core.user.OAuth2User;

import com.revnu.backend.features.auth.model.User;

public class GoogleUserAdapter {

    public static User.UserBuilder adaptGoogleUser(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String googleId = oAuth2User.getAttribute("sub");

        if (email == null) {
            throw new IllegalArgumentException("Google account did not provide an email address");
        }

        return User.builder()
                .email(email)
                .fullname(name != null ? name : email)
                .passwordHash("") 
                .oauthId(googleId);
    }
}