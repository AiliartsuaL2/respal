package com.hckst.respal.comment.domain.repository;

import com.hckst.respal.comment.domain.Comment;
import com.hckst.respal.comment.presentation.dto.response.CommentsResponseDto;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface CommentRepository extends R2dbcRepository<Comment,Long> {
    @Query("SELECT  c.id as id,"
            + "c.content as content, "
            + "c.x_location as xLocation,"
            + "c.y_location as yLocation,"
            + "c.delete_yn as deleteYn,"
            + "c.reg_time as regTime,"
            + "m.id as membersId,"
            + "m.email as membersEmail,"
            + "m.nickname as membersNickname,"
            + "m.picture as membersPicture \n"
            + "FROM comment c \n"
            + "INNER JOIN members m \n"
            + "ON c.members_id = m.id \n"
            + "where c.resume_id = :resumeId \n"
            + "and c.delete_yn = 'N'")
    Flux<Comment> findAllCommentsByResumeId(Long resumeId);
}