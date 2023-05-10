package com.hckst.respal.service;

import com.hckst.respal.common.exception.ErrorMessage;
import com.hckst.respal.repository.MembersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MembersService implements UserDetailsService {
    private final MembersRepository membersRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return membersRepository.findMembersByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorMessage.NOT_EXIST_MEMBER.getMsg()));
    }
}
