package com.hckst.respal.comment.domain.repository;

import com.hckst.respal.comment.domain.Comment;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface CommentRepository extends R2dbcRepository<Comment,Long> {
    Flux<Comment> findByResumeId(Long resumeId);
}
