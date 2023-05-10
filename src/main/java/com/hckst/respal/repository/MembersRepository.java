package com.hckst.respal.repository;

import com.hckst.respal.domain.Members;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembersRepository extends JpaRepository<Members,Long> {
    Optional<Members> findMembersByEmail(String email);
    Optional<Members> findMembersByEmailAndPassword(String email, String password);
}
