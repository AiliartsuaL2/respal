package com.hckst.respal.comment.domain.repository;

import com.hckst.respal.comment.domain.Comment;
import com.hckst.respal.converter.TFCode;
import com.hckst.respal.resume.domain.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment,Long> , CommentRepositoryCustom {
}
