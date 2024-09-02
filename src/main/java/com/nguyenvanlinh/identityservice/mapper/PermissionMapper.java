package com.nguyenvanlinh.identityservice.mapper;

import com.nguyenvanlinh.identityservice.dto.request.PermissionRequest;
import com.nguyenvanlinh.identityservice.dto.respone.PermissionResponse;
import com.nguyenvanlinh.identityservice.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse(Permission permission);
}
