package com.nguyenvanlinh.indentityservice.repository;

import com.nguyenvanlinh.indentityservice.entity.Role;
import com.nguyenvanlinh.indentityservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

    Optional<Role> findRoleByName(String Role);
}
