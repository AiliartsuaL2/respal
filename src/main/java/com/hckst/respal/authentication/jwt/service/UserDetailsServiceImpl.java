package com.hckst.respal.authentication.jwt.service;

import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.members.domain.repository.MembersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Jwt Provider 순환참조 방지용 Impl
@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    private final MembersRepository membersRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return membersRepository.findMembersByEmail(email)
                .orElseThrow(
                        () -> new UsernameNotFoundException(ErrorMessage.NOT_EXIST_MEMBER.getMsg())
                );
    }
}
