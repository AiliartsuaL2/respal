package com.hckst.respal.mention.domain.repository;

import com.hckst.respal.mention.domain.Mention;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentionRepository extends JpaRepository<Mention,Long> , MentionRepositoryCustom {
}
