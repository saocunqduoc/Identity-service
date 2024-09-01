package com.nguyenvanlinh.indentityservice.repository;

import com.nguyenvanlinh.indentityservice.entity.InvalidateToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidateTokenRepository extends JpaRepository<InvalidateToken, String> {

}
