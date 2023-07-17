package com.hckst.respal.comment.domain.repository;

import com.hckst.respal.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Long> {
}
