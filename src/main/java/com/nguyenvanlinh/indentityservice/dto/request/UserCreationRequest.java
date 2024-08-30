package com.nguyenvanlinh.indentityservice.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nguyenvanlinh.indentityservice.Validator.DobConstraint;
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

    @Size(min = 8, message = "PASSWORD_INVALID")
     String password;
     String firstName;
     String lastName;
    @DobConstraint(min = 18, message = "INVALID_DOB")
     LocalDate dob;
}
