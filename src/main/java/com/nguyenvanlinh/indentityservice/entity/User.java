package com.nguyenvanlinh.indentityservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Builder

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
// Tạo đầu tiên
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String username;
    String password;
    String firstName;
    String lastName;
    LocalDate dob;
    // Set là kểu lưu trữ (các phần tử )value unique

    // Chat gpt {
//    @ElementCollection
//    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
//    @Column(name = "Role")
    // }
    @ManyToMany
     Set<Role> roles;

    // Generate getter/ setter để Hibernates co thể map data
}
