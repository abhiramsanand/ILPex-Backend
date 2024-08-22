package com.ILPex.service.Impl;

import com.ILPex.entity.Roles;
import com.ILPex.repository.RoleRepository;
import com.ILPex.service.RolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RolesServiceImpl implements RolesService {
    @Autowired
    private RoleRepository rolesRepository;

    public Roles getRoleByName(String roleName) {
        return rolesRepository.findByRoleName(roleName);
    }

    public Roles createRole(String roleName) {
        Roles role = new Roles();
        role.setRoleName(roleName);
        return rolesRepository.save(role);
    }
}
