package com.nguyenvanlinh.indentityservice.controller;

import com.nguyenvanlinh.indentityservice.dto.request.ApiResponse;
import com.nguyenvanlinh.indentityservice.dto.request.PermissionRequest;
import com.nguyenvanlinh.indentityservice.dto.request.RoleRequest;
import com.nguyenvanlinh.indentityservice.dto.respone.PermissionResponse;
import com.nguyenvanlinh.indentityservice.dto.respone.RoleResponse;
import com.nguyenvanlinh.indentityservice.service.PermissionService;
import com.nguyenvanlinh.indentityservice.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping({"/roles","/roles/"}) // Khai báo toàn class
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @PostMapping
    ApiResponse<RoleResponse> create(@RequestBody RoleRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.create(request))
                .build();
    }
    @GetMapping
    ApiResponse<List<RoleResponse>> getAll() {
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAll())
                .build();
    }

    @DeleteMapping("/{role}")
    ApiResponse<Void> delete(@RequestBody @PathVariable String role) {
        roleService.delete(role);
        return ApiResponse.<Void>builder().build();
    }
}
