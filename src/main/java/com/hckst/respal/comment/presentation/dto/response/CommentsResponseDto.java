package com.hckst.respal.comment.presentation.dto.response;

import com.hckst.respal.comment.domain.Comment;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "댓글 응답 DTO")
public class CommentsResponseDto {
    @Schema(description = "댓글의 ID입니다.")
    private Long id;
    @Schema(description = "댓글의 타입입니다. (추가된 댓글 : ADDED, 기 존재 댓글 : EXISTS, 삭제된 댓글 : DELETED)")
    private CommentType commentType;
    @Schema(description = "댓글의 내용입니다.")
    private String content;
    @Schema(description = "댓글의 x좌표 입니다.")
    private int xLocation;
    @Schema(description = "댓글의 y좌표 입니다.")
    private int yLocation;
    @Schema(description = "해당 댓글이 존재하는 이력서 ID 입니다.")
    private Long resumeId;
    @Schema(description = "댓글 작성자의 ID 입니다.")
    private Long membersId;
    @Schema(description = "댓글 작성자의 이미지 url 입니다.")
    private String membersPicture;
    @Schema(description = "댓글 작성자의 닉네임 입니다.")
    private String membersNickname;
    @Schema(description = "댓글의 생성 일자 입니다.")
    private String regTime;

    @AllArgsConstructor
    @Getter
    private enum CommentType {
        ADDED("added"),
        EXISTS("exists"),
        DELETED("deleted");
        private final String name;
    }

    private static CommentsResponseDto of(Comment comment) {
        CommentsResponseDto commentsResponseDto = new CommentsResponseDto();
        commentsResponseDto.id = comment.getId();
        commentsResponseDto.content = comment.getContent();
        commentsResponseDto.xLocation = comment.getXLocation();
        commentsResponseDto.yLocation = comment.getYLocation();
        commentsResponseDto.resumeId = comment.getResumeId();
        commentsResponseDto.membersId = comment.getMembersId();
        commentsResponseDto.membersPicture = comment.getMembers().getPicture();
        commentsResponseDto.membersNickname = comment.getMembers().getNickname();
        commentsResponseDto.regTime = comment.getRegTime().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return commentsResponseDto;
    }

    public static CommentsResponseDto create(Comment comment) {
        CommentsResponseDto commentsResponseDto = of(comment);
        commentsResponseDto.commentType = CommentType.ADDED;
        return commentsResponseDto;
    }

    public static CommentsResponseDto convert(Comment comment) {
        CommentsResponseDto commentsResponseDto = of(comment);
        commentsResponseDto.commentType = CommentType.EXISTS;
        return commentsResponseDto;
    }

    public static CommentsResponseDto delete(Comment comment) {
        CommentsResponseDto commentsResponseDto = of(comment);
        commentsResponseDto.commentType = CommentType.DELETED;
        return commentsResponseDto;
    }
}
