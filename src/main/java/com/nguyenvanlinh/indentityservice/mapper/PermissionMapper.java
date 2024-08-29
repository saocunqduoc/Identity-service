package com.nguyenvanlinh.indentityservice.mapper;

import com.nguyenvanlinh.indentityservice.dto.request.PermissionRequest;
import com.nguyenvanlinh.indentityservice.dto.request.UserCreationRequest;
import com.nguyenvanlinh.indentityservice.dto.request.UserUpdateRequest;
import com.nguyenvanlinh.indentityservice.dto.respone.PermissionResponse;
import com.nguyenvanlinh.indentityservice.dto.respone.UserResponse;
import com.nguyenvanlinh.indentityservice.entity.Permission;
import com.nguyenvanlinh.indentityservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse(Permission permission);
}
