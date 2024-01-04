package com.hckst.respal.tag.domain.repository;

import com.hckst.respal.tag.domain.Tag;

import java.util.Optional;

public interface TagRepositoryCustom {
    public Optional<Tag> findTagAndResumeByMembersId(Long membersId);
}
