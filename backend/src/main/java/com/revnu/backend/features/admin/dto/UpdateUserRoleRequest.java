package com.revnu.backend.features.admin.dto;

import com.revnu.backend.features.auth.model.RoleType;

public record UpdateUserRoleRequest(RoleType role) {

}
