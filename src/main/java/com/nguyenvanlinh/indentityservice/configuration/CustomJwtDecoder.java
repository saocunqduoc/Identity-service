package com.nguyenvanlinh.indentityservice.configuration;

import com.nguyenvanlinh.indentityservice.dto.request.IntrospectRequest;
import com.nguyenvanlinh.indentityservice.exception.AppException;
import com.nguyenvanlinh.indentityservice.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.extern.flogger.Flogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithms;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;
import java.util.*;

@Slf4j
@Component
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${jwt.signerKey}")
    private String signerKey;

    @Autowired
    private AuthenticationService authenticationService;

    private NimbusJwtDecoder nimbusJwtDecoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            var response = authenticationService.introspect(new IntrospectRequest(token));
            if(!response.isValid()) {
                throw new BadJwtException("Invalid token");
            }
        } catch (ParseException | JOSEException e) {
            throw new BadJwtException(e.getMessage());
        }
        if(Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder
                            .withSecretKey(secretKeySpec)
                            .macAlgorithm(MacAlgorithm.HS512)
                            .build();
        }
        return nimbusJwtDecoder.decode(token);
    }


}
