package com.hckst.respal.mention.domain.repository;

import com.hckst.respal.mention.domain.Mention;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MentionJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<Mention> mentionList) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO mention (`members_id`, `resume_id`,`reg_time`) VALUES (?, ?, ?)",
                mentionList,	// insert할 데이터 리스트
                20,	// 1회에 진행할 배치 사이즈(10만개 데이터라면 만 개 씩 10번 돌아감)
                (ps, mention) -> {
                    ps.setLong(1, mention.getMembers().getId());
                    ps.setLong(2, mention.getResume().getId());
                    ps.setTimestamp(3,java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
                });
    }
}
