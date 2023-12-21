package com.hckst.respal.comment.domain.repository;

import com.hckst.respal.comment.domain.Comment;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommentRepository extends R2dbcRepository<Comment,Long> {
    @Query(   "SELECT  c.id as id,"
                + "c.content as content, "
                + "c.x_location as xLocation,"
                + "c.y_location as yLocation,"
                + "c.delete_yn as deleteYn,"
                + "c.reg_time as regTime,"
                + "m.id as membersId,"
                + "m.email as membersEmail,"
                + "m.nickname as membersNickname,"
                + "m.picture as membersPicture,"
                + "r.id as resumeId,"
                + "r.delete_yn as resumeDeleteYn,"
                + "r.members_id as resumeMembersId \n"
            + "FROM comment c \n"
            + "INNER JOIN members m \n"
                + "ON c.members_id = m.id \n"
            + "INNER JOIN resume r \n"
                + "ON c.resume_id = r.id \n"
            + "WHERE c.resume_id = :resumeId \n"
            + "AND c.delete_yn = 'N'")
    Flux<Comment> findAllCommentsByResumeId(Long resumeId);

    @Query(   "SELECT  c.id as id,"
                + "c.content as content, "
                + "c.x_location as xLocation,"
                + "c.y_location as yLocation,"
                + "c.delete_yn as deleteYn,"
                + "c.reg_time as regTime,"
                + "m.id as membersId,"
                + "m.email as membersEmail,"
                + "m.nickname as membersNickname,"
                + "m.picture as membersPicture,"
                + "r.id as resumeId,"
                + "r.delete_yn as resumeDeleteYn,"
                + "r.members_id as resumeMembersId \n"
            + "FROM comment c \n"
            + "INNER JOIN members m \n"
                + "ON c.members_id = m.id \n"
            + "INNER JOIN resume r \n"
                + "ON c.resume_id = r.id \n"
            + "where c.id = :commentId")
    Mono<Comment> findCommentWithResumeById(Long commentId);
}