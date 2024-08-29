package com.nguyenvanlinh.indentityservice.repository;

import com.nguyenvanlinh.indentityservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
}
