package com.example.projectBackEnd.service.impl;

import com.example.projectBackEnd.entity.Role;
import com.example.projectBackEnd.repo.RoleRepo;
import com.example.projectBackEnd.service.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepo roleRepo;

    public RoleServiceImpl(RoleRepo repo) {
        this.roleRepo = repo;
    }

    @Override
    public Role createNewRole(Role role) {
        return roleRepo.save(role);
    }
}
