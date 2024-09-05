package com.nguyenvanlinh.identityservice.Service;

import com.nguyenvanlinh.identityservice.Validator.DobConstraint;
import com.nguyenvanlinh.identityservice.dto.request.UserCreationRequest;
import com.nguyenvanlinh.identityservice.dto.request.UserUpdateRequest;
import com.nguyenvanlinh.identityservice.dto.response.RoleResponse;
import com.nguyenvanlinh.identityservice.dto.response.UserResponse;
import com.nguyenvanlinh.identityservice.entity.Role;
import com.nguyenvanlinh.identityservice.entity.User;
import com.nguyenvanlinh.identityservice.exception.AppException;
import com.nguyenvanlinh.identityservice.exception.ErrorCode;
import com.nguyenvanlinh.identityservice.mapper.UserMapper;
import com.nguyenvanlinh.identityservice.mapper.UserMapperImpl;
import com.nguyenvanlinh.identityservice.repository.RoleRepository;
import com.nguyenvanlinh.identityservice.repository.UserRepository;
import com.nguyenvanlinh.identityservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private RoleRepository roleRepository;

    private UserCreationRequest request;
    private UserUpdateRequest userUpdateRequest;
    private UserMapper userMapper;
    private UserResponse userResponse;
    private User user;
    private LocalDate dob;
    private Role role;

    @BeforeEach
    void initData(){

        userMapper = new UserMapperImpl();
        role = Role.builder()
                .name("ROLE_USER").description("User default role")
                .build();
        roleRepository.save(role);

        dob = LocalDate.of(1990, 1, 1);

        request = UserCreationRequest.builder()
                .username("saocunqduoc")
                .firstName("Linh")
                .lastName("Van Nguyen")
                .password("12345678910")
                .dob(dob)
                .build();
        userUpdateRequest = UserUpdateRequest.builder()
                .password("1@Linh2003")
                .lastName("Nguyen Van")
                .roles(Set.of(role))
                .build();

        userResponse = UserResponse.builder()
                .id("cf0600f538b3")
                .username("saocunqduoc")
                .firstName("Linh")
                .lastName("Van Nguyen")
                .dob(dob)
                .build();

        user = User.builder()
                .id("cf0600f538b3")
                .username("saocunqduoc")
                .firstName("Linh")
                .lastName("Van Nguyen")
                .dob(dob)
                .build();
    }

    @Test
    void createUser_validRequest_success(){
        // GIVEN
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);
        when(roleRepository.findRoleByName("ROLE_USER")).thenReturn(Optional.of(role));
        // WHEN
        var user = userService.createUser(request);

        // THEN
        Assertions.assertThat(user.getId()).isEqualTo("cf0600f538b3");
        Assertions.assertThat(user.getUsername()).isEqualTo("saocunqduoc");
    }

    @Test
    void createUser_userExisted_fail(){
        // GIVEN
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // WHEN
        var exception = assertThrows(AppException.class,
                () -> userService.createUser(request));

        // THEN
        Assertions.assertThat(exception.getErrorCode().getCode())
                .isEqualTo(1002);
    }
    // createUser_ Role not found
    @Test
    void createUser_RoleNotFound_fail(){
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);
        when(roleRepository.findRoleByName("ROLE_USER")).thenReturn(Optional.empty());

        var exception = assertThrows(AppException.class, () -> userService.createUser(request));
        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1008);
    }
    // get Info success
    @Test
    @WithMockUser(username = "saocunqduoc") // mock user => khong can authentication
    void getMyInfo_validRequest_success(){
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        var resp = userService.getMyInfo();
        Assertions.assertThat(resp.getUsername()).isEqualTo("saocunqduoc");
    }
    // get Info fail
    @Test
    @WithMockUser(username = "saocunqduoc") // mock user => khong can authentication
    void getMyInfo_userNotFound_Error(){
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.ofNullable(null));

        var exception = assertThrows(AppException.class, () -> userService.getMyInfo());
        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1005);
    }
    // delete user
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteUser_validRequest_success(){
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        userService.deleteUser(user.getId());
    }
    // get Users success
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getUsers_validRequest_success(){
        when(userRepository.findAll().stream().toList()).thenReturn(List.of(user));

        var resp = userService.getUsers();
        Assertions.assertThat(resp).isNotNull();
    }
    // get User by ID success
    @Test
    @WithMockUser(username = "saocunqduoc", roles = {"USER"})
    void getUserById_validRequest_success(){
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
        var resp = userService.getUser("cf0600f538b3");
        Assertions.assertThat(resp.getId()).isEqualTo("cf0600f538b3");
    }
    // getUser by ID fail
    @Test
    @WithMockUser(username = "saocunqduoc", roles = {"USER"})
    void getUserById_userNotExist_fail(){
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        var exception = assertThrows(AppException.class, () -> userService.getUser("cf0600f538b3"));
        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1005);
    }
    // updateUser
    @Test
    @WithMockUser(username = "saocunqduoc", roles = {"USER"})
    void updateUser_validRequest_success() {
        // GIVEN
        // đúng theo thứ tự trong update Service
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
        userService.updateUser(user.getId(), userUpdateRequest);
        when(userRepository.save(any())).thenReturn(user);
        // WHEN
        var mapper = userMapper.toUserResponse(userRepository.save(user));
        // THEN
        Assertions.assertThat(mapper.getUsername()).isEqualTo("saocunqduoc");
        Assertions.assertThat(mapper.getLastName()).isEqualTo("Nguyen Van");
    }
    // updateUser fail
    @Test
    @WithMockUser(username = "saocunqduoc", roles = {"USER"})
    void updateUser_userNotExist_fail() {
        // GIVEN
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());
        // WHEN
        var mapper = assertThrows(AppException.class,
                () -> userService.updateUser(user.getId(), userUpdateRequest));
        // THEN
        Assertions.assertThat(mapper.getErrorCode().getCode()).isEqualTo(1005);
    }

}