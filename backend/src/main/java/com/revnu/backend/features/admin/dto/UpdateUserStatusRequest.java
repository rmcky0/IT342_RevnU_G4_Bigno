package com.revnu.backend.features.admin.dto;

import com.revnu.backend.features.auth.model.AccountStatus;

public record UpdateUserStatusRequest(AccountStatus status) {

}
