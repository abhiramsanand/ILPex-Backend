package com.ILPex.service;

import com.ILPex.entity.Roles;

public interface RolesService {
    public Roles getRoleByName(String roleName);
    public Roles createRole(String roleName);
}
