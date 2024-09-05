package com.nguyenvanlinh.identityservice.dto.request;

import com.nguyenvanlinh.identityservice.Validator.DobConstraint;
import com.nguyenvanlinh.identityservice.entity.Role;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    @Size(min = 8, message = "PASSWORD_INVALID")
     String password;
     String firstName;
     String lastName;
    @DobConstraint(min = 18, message = "INVALID_DOB")
     LocalDate dob;
     Set<Role> roles;
}
