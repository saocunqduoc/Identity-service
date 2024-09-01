package com.nguyenvanlinh.indentityservice.service;

import com.nguyenvanlinh.indentityservice.dto.request.AuthenticationRequest;
import com.nguyenvanlinh.indentityservice.dto.request.IntrospectRequest;
import com.nguyenvanlinh.indentityservice.dto.request.LogoutRequest;
import com.nguyenvanlinh.indentityservice.dto.respone.AuthenticationResponse;
import com.nguyenvanlinh.indentityservice.dto.respone.IntrospectResponse;
import com.nguyenvanlinh.indentityservice.entity.InvalidateToken;
import com.nguyenvanlinh.indentityservice.entity.User;
import com.nguyenvanlinh.indentityservice.exception.AppException;
import com.nguyenvanlinh.indentityservice.exception.ErrorCode;
import com.nguyenvanlinh.indentityservice.repository.InvalidateTokenRepository;
import com.nguyenvanlinh.indentityservice.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashSet;
import java.util.StringJoiner;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;

    InvalidateTokenRepository invalidateTokenRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    //
    public IntrospectResponse introspect(IntrospectRequest request)
            throws JOSEException, ParseException {
        // get token từ request của authenticate
        var token = request.getToken();
        boolean isValid = true;
        try {
            // verify Jwt
            verifyToken(token);
        } catch (AppException e) {
            // nếu chưa verify/ expiration/ đã logout -> false
            isValid = false;
        }
        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }
    // Xác thực
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // get Username
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        // matches password
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if(!authenticated) {
            throw new AppException(ErrorCode.WRONG_USERNAME_PASSWORD);
        }

        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    // logout
    public void logOut(LogoutRequest request) throws ParseException, JOSEException {
        var signToken = verifyToken(request.getToken());

        String jti = signToken.getJWTClaimsSet().getJWTID();
        Date expirationTime = signToken.getJWTClaimsSet().getExpirationTime();

        InvalidateToken invalidateToken = InvalidateToken.builder()
                .id(jti)
                .expirationTime(expirationTime)
                .build();

        invalidateTokenRepository.save(invalidateToken);
    }

    private SignedJWT verifyToken(String token)
            throws JOSEException, ParseException {
        // Verify -> truyền vào signkey để mã hóa
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        // parse token -> String và add exception
        SignedJWT signedJWT = SignedJWT.parse(token);

        // Kiểm tra đã hết hạn chưa
        Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();

        // thực thi verify
        var verify = signedJWT.verify(verifier); // nếu đúng -> true else -> false
        // nếu chưa được verify và jwt đã hết hạn => unauthenticate
        if(!(verify && expiration.after(new Date()))) // check expiration có after now kh nếu đúng => true
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        if(invalidateTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }
    // tạo token
    public String generateToken(User user) {
        // Header
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);// thuật toán HS512

        // Data trong body(nội dung gửi đi trong token) -> gọi là claim
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername()) // đại diện cho user đang nhập
                .issuer("nguyenvanlinh.com") // được issue từ ai? Thường là domain của Service
                .issueTime(new Date()) // thời gian tạo
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                )) // set time hết hạn vd 1 tiếng
                // tạo ID để thao tác như logout/ refresh
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user)) // scope take role of user
                .build();

        //Payload: nhận vào claimset
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        // cần Header và Payload
        JWSObject jwsObject = new JWSObject(jwsHeader,payload);

        // Tiếp đến cần ký token : String metric dùng chung khóa ký và giải. Còn cách khác
        // lên https://generate-random.org/encryption-key-generator để key chuỗi 32 bytes 256 bit
        // vd : "SE1f0TDucS8RvuRNvX+EHYc5VeBEJH1BVJQs8zIhgtqFU343Wo3Qdkw3QavKD0eP"
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.info("Cannot create token!");
            throw new RuntimeException(e);
        }
    }

    // build scope từ một user to get role -> claim ở payload
    // update: get user -> get roles bao gồm cả permissions của roles
    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        // add roles
        if(!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                // add permissions
                if(!CollectionUtils.isEmpty(role.getPermissions())) {
                    role.getPermissions().forEach(permission -> {
                        stringJoiner.add(permission.getName());
                    });
                }

            });
        }
        return stringJoiner.toString();
    }
}
