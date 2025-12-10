package com.ssafy.bbb.model.service;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ssafy.bbb.model.dao.UserDao;
import com.ssafy.bbb.model.dto.user.UserDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userDao.findByEmail(email)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));
    }

    // DB의 UserDto -> Security의 UserDetails로 변환
    private UserDetails createUserDetails(UserDto user) {
        return new User(
                user.getEmail(), // username (email 사용)
                user.getPassword(), // 암호화된 비밀번호
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().toString())) // 권한
        );
    }
}