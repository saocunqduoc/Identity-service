package com.nguyenvanlinh.identityservice.configuration;

import com.nguyenvanlinh.identityservice.entity.Permission;
import com.nguyenvanlinh.identityservice.entity.User;
import com.nguyenvanlinh.identityservice.entity.Role;
import com.nguyenvanlinh.identityservice.repository.PermissionRepository;
import com.nguyenvanlinh.identityservice.repository.RoleRepository;
import com.nguyenvanlinh.identityservice.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;

    @Bean
    @ConditionalOnProperty(prefix = "spring",
            value = "datasource.driverClassName",
            havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner(
            UserRepository userRepository, PermissionRepository permissionRepository, RoleRepository roleRepository
    ) {
        return args -> {
            // Permissions
            Permission approvePost = new Permission("APPROVE_POST", "Approve post permission");
            Permission createPost = new Permission("CREATE_POST", "Create post permission");
            Permission rejectPost = new Permission("REJECT_POST", "Reject post permission");

            if (permissionRepository.count() == 0) {
                permissionRepository.saveAll(Set.of(approvePost, createPost, rejectPost));
            }

            // Roles
            Role adminRole = new Role("ADMIN", "Admin Role", Set.of(approvePost, createPost, rejectPost));
            Role userRole = new Role("USER", "User Role", Set.of(createPost));

            if (roleRepository.count() == 0) {
                roleRepository.saveAll(Set.of(adminRole, userRole));
            }
            // Admin
            if (userRepository.findByUsername("admin").isEmpty() ) {
                User user = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .roles(Set.of(adminRole))
                        .build();
                userRepository.save(user);
                log.warn("admin user has been created with default password: admin, please change it!");
            }
        };
    }
}
