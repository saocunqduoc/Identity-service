package com.nguyenvanlinh.indentityservice.mapper;

import com.nguyenvanlinh.indentityservice.dto.request.UserCreationRequest;
import com.nguyenvanlinh.indentityservice.dto.request.UserUpdateRequest;
import com.nguyenvanlinh.indentityservice.dto.respone.UserResponse;
import com.nguyenvanlinh.indentityservice.entity.User;
import org.mapstruct.Mapper;
// define map data từ Request -> Object
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    // @Mapping(source = "firstName", target = "lastName") set lastname = giá trị với firstname.
    // @Mapping(target = "lastName", ignore = true) => lastName = null.
    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
