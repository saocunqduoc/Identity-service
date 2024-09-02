package com.nguyenvanlinh.identityservice.dto.request;

import com.nguyenvanlinh.identityservice.Validator.DobConstraint;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

// Lombok
//----------
@Getter
@Setter
@Data
// No constructor
@NoArgsConstructor
// All attribute constructor
@AllArgsConstructor
@Builder
// modify chung -> không cần khai báo field
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Size(min = 6, message = "USERNAME_INVALID") // tên của Error Code
     String username;

    @Size(min = 10, message = "PASSWORD_INVALID")
     String password;
     String firstName;
     String lastName;
    @DobConstraint(min = 15, message = "INVALID_DOB")
     LocalDate dob;
}
