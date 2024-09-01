package com.nguyenvanlinh.indentityservice.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IntrospectRequest {
    String token;
}
