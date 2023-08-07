package com.hckst.respal.mention.domain.repository;

import com.hckst.respal.mention.domain.Mention;

import java.util.Optional;

public interface MentionRepositoryCustom {
    public Optional<Mention> findMentionAndResumeById(Long mentionId);
}
