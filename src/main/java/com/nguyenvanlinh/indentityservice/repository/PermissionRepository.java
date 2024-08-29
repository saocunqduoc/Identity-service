package com.nguyenvanlinh.indentityservice.repository;

import com.nguyenvanlinh.indentityservice.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {

}
