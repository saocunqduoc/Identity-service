package com.nguyenvanlinh.indentityservice.dto.respone;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.JoinColumn;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

// Trả về
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
     String id;
     String username;
     String firstName;
     String lastName;
     LocalDate dob;
     // set make (các phần tử )value unique
     // Chat gpt {
//     @ElementCollection
//     @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
//     @Column(name = "role")
     // }
     Set<RoleResponse> roles;
}
