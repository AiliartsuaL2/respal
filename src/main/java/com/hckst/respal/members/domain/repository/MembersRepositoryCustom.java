package com.hckst.respal.members.domain.repository;

import com.hckst.respal.converter.Provider;
import com.hckst.respal.members.domain.Members;

import java.util.Optional;

public interface MembersRepositoryCustom {
    Optional<Members> findMembersOauth(String email, Provider provider);
}
