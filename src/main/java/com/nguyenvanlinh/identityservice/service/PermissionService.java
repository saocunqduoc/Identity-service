package com.nguyenvanlinh.identityservice.service;

import com.nguyenvanlinh.identityservice.dto.request.PermissionRequest;
import com.nguyenvanlinh.identityservice.dto.respone.PermissionResponse;
import com.nguyenvanlinh.identityservice.entity.Permission;
import com.nguyenvanlinh.identityservice.mapper.PermissionMapper;
import com.nguyenvanlinh.identityservice.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {

    PermissionRepository permissionRepository;

    PermissionMapper permissionMapper;

    public PermissionResponse create(PermissionRequest permissionRequest){
        Permission permission = permissionMapper.toPermission(permissionRequest);
        permission = permissionRepository.save(permission);

        return permissionMapper.toPermissionResponse(permission);
    }

    public List<PermissionResponse> getAll() {
        var permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
    }

    public void delete(String permission){
        permissionRepository.deleteById(permission);
    }
}
