package com.nguyenvanlinh.indentityservice.service;

import com.nguyenvanlinh.indentityservice.dto.request.ApiResponse;
import com.nguyenvanlinh.indentityservice.dto.request.UserCreationRequest;
import com.nguyenvanlinh.indentityservice.dto.request.UserUpdateRequest;
import com.nguyenvanlinh.indentityservice.dto.respone.UserResponse;
import com.nguyenvanlinh.indentityservice.entity.User;
import com.nguyenvanlinh.indentityservice.enums.Role;
import com.nguyenvanlinh.indentityservice.exception.AppException;
import com.nguyenvanlinh.indentityservice.exception.ErrorCode;
import com.nguyenvanlinh.indentityservice.mapper.UserMapper;
import com.nguyenvanlinh.indentityservice.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    // ? dùng để làm gì
     UserRepository userRepository;

     UserMapper userMapper;

     PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;

    public User createUser(UserCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        User user = userMapper.toUser(request);
        // encode: mã hóa. matches: kiểm trả mật khẩu có trùng không. upgradeCoding ...
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // set role onboard
        HashSet<String> roles = new HashSet<>();
        // add role
        roles.add(Role.USER.name());
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        context.getAuthentication();
        User user = userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserResponse(user);
    }

    // thực hiện việc xác thực trước khi truy cập vào hàm
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getUsers() {
        log.info("In getUsers method"); // Nếu không có ROLE ADMIN sẽ không thể truy cập Method -> không hiện log
        return userRepository.findAll();
    }
    // nếu List<UserResponse> sử dụng thì stream và userMapper
//    public List<UserResponse> getUsers(){
//        return userRepository.findAll().stream()
//                .map(userMapper::toUserResponse).toList();
//    }
    // thực hiện trước rồi mới xác thực ROLE
//    @PostAuthorize("hasRole('ADMIN')")
    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getUser(String id) {
        log.info("In getUser method"); // Sẽ truy cập Method và hiện log bất kể có ROLE hay không. Có -> return. Không -> Acces denied
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    public UserResponse updateUser(String idUser, UserUpdateRequest request) {
        User user =  userRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userMapper.updateUser(user, request);

//        cách cũ
//        User user = getUser(idUser);
//        user.setPassword(request.getPassword());
//        user.setFirstName(request.getFirstName());
//        user.setLastName(request.getLastName());
//        user.setDob(request.getDob());

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteUser(String idUser) {
        userRepository.deleteById(idUser);
    }
}
