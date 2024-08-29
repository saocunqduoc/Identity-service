package com.nguyenvanlinh.indentityservice.mapper;

import com.nguyenvanlinh.indentityservice.dto.request.RoleRequest;
import com.nguyenvanlinh.indentityservice.dto.respone.RoleResponse;
import com.nguyenvanlinh.indentityservice.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);
    RoleResponse toRoleResponse(Role role);
}
