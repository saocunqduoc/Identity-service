package com.nguyenvanlinh.identityservice.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nguyenvanlinh.identityservice.constant.RolesConstant;
import com.nguyenvanlinh.identityservice.dto.request.UserCreationRequest;
import com.nguyenvanlinh.identityservice.dto.request.UserUpdateRequest;
import com.nguyenvanlinh.identityservice.dto.response.UserResponse;
import com.nguyenvanlinh.identityservice.entity.Role;
import com.nguyenvanlinh.identityservice.entity.User;
import com.nguyenvanlinh.identityservice.exception.AppException;
import com.nguyenvanlinh.identityservice.exception.ErrorCode;
import com.nguyenvanlinh.identityservice.mapper.UserMapper;
import com.nguyenvanlinh.identityservice.repository.RoleRepository;
import com.nguyenvanlinh.identityservice.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    // ? dùng để làm gì
    UserRepository userRepository;

    RoleRepository roleRepository;

    UserMapper userMapper;

    PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserCreationRequest request) {
        User user = userMapper.toUser(request);
        // encode: mã hóa. matches: kiểm trả mật khẩu có trùng không. upgradeCoding ...
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // set role onboard
        HashSet<Role> roles = new HashSet<>();
        // them role neu ton tai. role user
        roleRepository.findById(RolesConstant.USER_ROLE).ifPresent(roles::add);
        user.setRoles(roles);
        // neu khong co exception -> save -> lv performance
        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        return userMapper.toUserResponse(user);
    }

    // getInfo bằng token authenticate hiện tại -> Không cần endpoint là id user
    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        context.getAuthentication();
        User user = userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        log.info("In GET myInfo method");
        return userMapper.toUserResponse(user);
    }

    // thực hiện việc xác thực trước khi truy cập vào hàm
    @PreAuthorize("hasRole('ADMIN')")
    // không phù hợp vì khi gọi role sẽ ưu tiên tìm role thay vì quyền
    // => nên gọi hasAuthority thay vì hasRole vì 1 người có thể có nhiều Role
    //    @PreAuthorize("hasAuthority('APPROVE_POST')")
    public List<UserResponse> getUsers() {
        log.info("In GET Users method"); // Nếu không có ROLE ADMIN sẽ không thể truy cập Method -> không hiện log
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getUser(String id) {
        log.info(
                "In getUser method"); // Sẽ truy cập Method và hiện log bất kể có ROLE hay không. Có -> return. Không ->
        // Acces denied
        return userMapper.toUserResponse(
                userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    public UserResponse updateUser(String idUser, UserUpdateRequest request) {
        User user = userRepository.findById(idUser).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        // kiểm tra xem tài khoản muốn update có giống với thông tin xác thực không
        // mapper update
        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // convert convert để get role hiện tại của user
        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String idUser) {
        userRepository.deleteById(idUser);
    }
}
