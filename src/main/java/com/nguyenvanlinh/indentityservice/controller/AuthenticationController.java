package com.nguyenvanlinh.indentityservice.controller;

import com.nguyenvanlinh.indentityservice.dto.request.ApiResponse;
import com.nguyenvanlinh.indentityservice.dto.request.AuthenticationRequest;
import com.nguyenvanlinh.indentityservice.dto.request.IntrospectRequest;
import com.nguyenvanlinh.indentityservice.dto.respone.AuthenticationResponse;
import com.nguyenvanlinh.indentityservice.dto.respone.IntrospectResponse;
import com.nguyenvanlinh.indentityservice.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        var result = authenticationService.authenticate(request);

        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);

        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }
//    nếu không sử dụng  mapstruct
//    @PostMapping("/log-in")
//    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
//        boolean result = authenticationService.authenticate(request);
//
//        // Tạo đối tượng AuthenticationResponse bằng cách sử dụng setter methods
//        AuthenticationResponse authResponse = new AuthenticationResponse();
//        authResponse.setAuthenticated(result);
//
//        // Tạo đối tượng ApiResponse bằng cách sử dụng setter methods
//        ApiResponse<AuthenticationResponse> response = new ApiResponse<>();
//        response.setResult(authResponse);
//
//        return response;
//    }
}
