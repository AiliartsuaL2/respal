package com.hckst.respal.repository;

import com.hckst.respal.domain.Members;
import com.hckst.respal.repository.custom.MembersRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembersRepository extends JpaRepository<Members,Long>, MembersRepositoryCustom {
    Optional<Members> findMembersByEmail(String email);
    Optional<Members> findMembersByEmailAndPassword(String email, String password);
    boolean existsMembersByEmail(String email);
}
