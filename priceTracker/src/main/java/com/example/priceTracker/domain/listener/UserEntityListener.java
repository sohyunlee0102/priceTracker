package com.example.priceTracker.domain.listener;

import com.example.priceTracker.apiPayload.code.status.ErrorStatus;
import com.example.priceTracker.apiPayload.exception.handler.UserHandler;
import com.example.priceTracker.domain.enums.UserStatus;
import com.example.priceTracker.domain.user.User;
import jakarta.persistence.PostLoad;

public class UserEntityListener {

    @PostLoad
    public void filterInactiveUsers(User user) {
        if (!ListenerUtil.isListenerEnabled()) {
            return;
        }

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new UserHandler(ErrorStatus.INACTIVE_USER);
        }
    }

}
