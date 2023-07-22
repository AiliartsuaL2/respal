package com.hckst.respal.comment.domain.repository;

import com.hckst.respal.comment.presentation.dto.response.CommentsResponseDto;
import com.hckst.respal.resume.domain.Resume;

import java.util.List;
import java.util.Optional;

public interface CommentRepositoryCustom {
    Optional<List<CommentsResponseDto>> findCommentsDtoByResume(Resume resume);
}
