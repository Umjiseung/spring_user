package com.test.auth.domain.service;


import com.test.auth.domain.data.dto.entity.User;
import com.test.auth.domain.repository.UserRepository;
import com.test.auth.global.exception.AppException;
import com.test.auth.global.exception.ErrorCode;
import com.test.auth.global.utils.JwtUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder encoder;


    @Value("${jwt.secret}")
    private String secretKey;

    private Long expiredMs = 1000 * 60 * 60L;

    public String join(String userName, String password) {

        // userName 중복 체크
        userRepository.findByUserName(userName)
                .ifPresent(user -> {
                    throw new AppException(ErrorCode.USERNAME_DUPLICATED, userName + "는(은) 이미 있습니다.");
                });

        // userName 저장
        User user = User.builder()
                        .userName(userName)
                        .password(encoder.encode(password))
                        .build();

        userRepository.save(user);
        ;
        return "SUCCESS";
    }

    public String login(String userName, String password) {
        // userName 없음
        User selectedUser = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, userName + "이 없습니다."));

        // password 틀림
        if (!encoder.matches(password, selectedUser.getPassword()))
            throw new AppException(ErrorCode.INVALID_PASSWORD, "PW를 잘못 입력 하셨습니다.");

        return JwtUtil.createJwt(userName, secretKey, expiredMs);


    }
}