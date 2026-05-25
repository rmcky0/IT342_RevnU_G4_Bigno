package com.revnu.backend.features.auth.observer;

import com.revnu.backend.features.auth.model.User;

public interface AuthEventListener {

    void onUserRegistered(User user);
}
