package com.nguyenvanlinh.identityservice.repository;

import com.nguyenvanlinh.identityservice.entity.InvalidateToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidateTokenRepository extends JpaRepository<InvalidateToken, String> {

}
