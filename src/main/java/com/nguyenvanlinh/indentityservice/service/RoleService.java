package com.nguyenvanlinh.indentityservice.service;

import com.nguyenvanlinh.indentityservice.dto.request.RoleRequest;
import com.nguyenvanlinh.indentityservice.dto.respone.RoleResponse;
import com.nguyenvanlinh.indentityservice.mapper.RoleMapper;
import com.nguyenvanlinh.indentityservice.repository.PermissionRepository;
import com.nguyenvanlinh.indentityservice.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;

    PermissionRepository permissionRepository;

    RoleMapper roleMapper;
    // khác với tạo permission
    public RoleResponse create(RoleRequest request){
        var role = roleMapper.toRole(request);

        var getPermissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(getPermissions));

        role = roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    public List<RoleResponse> getAll(){
        var roles = roleRepository.findAll();
        return roles.stream().map(roleMapper::toRoleResponse).toList();
    }

    public void delete(String role) {
        roleRepository.deleteById(role);
    }
}
