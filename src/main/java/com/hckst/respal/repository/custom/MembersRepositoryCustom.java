package com.hckst.respal.repository.custom;

import com.hckst.respal.common.converter.Provider;
import com.hckst.respal.domain.Members;

import java.util.Optional;

public interface MembersRepositoryCustom {
    Optional<Members> findMembersOauth(String email, Provider provider);
}
