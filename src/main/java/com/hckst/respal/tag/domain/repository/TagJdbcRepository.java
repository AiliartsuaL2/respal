package com.hckst.respal.tag.domain.repository;

import com.hckst.respal.tag.domain.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TagJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<Tag> tagList) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO tag (`members_id`, `resume_id`,`reg_time`) VALUES (?, ?, ?)",
                tagList,	// insert할 데이터 리스트
                20,	// 1회에 진행할 배치 사이즈(10만개 데이터라면 만 개 씩 10번 돌아감)
                (ps, tag) -> {
                    ps.setLong(1, tag.getMembers().getId());
                    ps.setLong(2, tag.getResume().getId());
                    ps.setTimestamp(3,java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
                });
    }
}
