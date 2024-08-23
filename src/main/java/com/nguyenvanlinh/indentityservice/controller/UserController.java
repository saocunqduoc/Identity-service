package com.nguyenvanlinh.indentityservice.controller;

import com.nguyenvanlinh.indentityservice.dto.request.ApiResponse;
import com.nguyenvanlinh.indentityservice.dto.request.UserCreationRequest;
import com.nguyenvanlinh.indentityservice.dto.request.UserUpdateRequest;
import com.nguyenvanlinh.indentityservice.dto.respone.UserResponse;
import com.nguyenvanlinh.indentityservice.entity.User;
import com.nguyenvanlinh.indentityservice.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping({"/users","/users/"}) // Khai báo toàn class
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    // @PostMapping("/users") -> không cần khai báo chi tiết
    // Post là tạo
    // @Valid để thực hiện validate được khai báo trong request
    @PostMapping
    ApiResponse<User> createUser(@RequestBody @Valid UserCreationRequest request) {
        ApiResponse<User> apiResponse = new ApiResponse<>();

        apiResponse.setResult(userService.createUser(request));

        return apiResponse;
    }
    // Lấy ds User
    @GetMapping
    ApiResponse<List<User>> getUsers() {
        // get SCOPE đang được authenticate. vd: SCOPE_ADMIN, SCOPE_USER -> dùng để authenticate ở Security Cofig
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Username : {}", authentication.getName());
        authentication.getAuthorities().forEach(
                grantedAuthority -> {
                    log.info(grantedAuthority.getAuthority());
                }
        );

        return ApiResponse.<List<User>>builder()
                .result(userService.getUsers())
                .build();
    }
    // "/users/{userId}"
    @GetMapping("/{userId}")
    UserResponse getUser(@PathVariable("userId") String userId) {
        return userService.getUser(userId);
    }

    // Put -> Update data
    // sử dụng Request -> request body
    @PutMapping("/{userId}")
    UserResponse updateUser(@PathVariable("userId") String userId,@Valid @RequestBody UserUpdateRequest request) {
        return userService.updateUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    String deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return "User " + userId + "has been deleted!!~";
    }
}
