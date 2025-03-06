package com.example.projectBackEnd.repo;

import com.example.projectBackEnd.entity.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepo extends CrudRepository<Role,String> {
}
