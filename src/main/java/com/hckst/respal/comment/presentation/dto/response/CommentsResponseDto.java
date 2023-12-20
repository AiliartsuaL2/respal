package com.hckst.respal.comment.presentation.dto.response;

import com.hckst.respal.comment.domain.Comment;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class CommentsResponseDto {
    private Long id;
    private CommentType commentType;
    private String content;
    private int xLocation;
    private int yLocation;
    private Long membersId;
    private String membersPicture;
    private String membersNickname;
    private String regTime;

    @AllArgsConstructor
    @Getter
    private enum CommentType {
        ADDED("added"),
        EXISTS("exists"),
        DELETED("deleted");
        private final String name;
    }

    @Builder
    public CommentsResponseDto(Long id, String content, int xLocation, int yLocation, Long membersId, String membersPicture, String membersNickname, LocalDateTime regTime) {
        this.id = id;
        this.content = content;
        this.xLocation = xLocation;
        this.yLocation = yLocation;
        this.membersId = membersId;
        this.membersPicture = membersPicture;
        this.membersNickname = membersNickname;
        this.regTime = regTime != null ? regTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")) : null;
    }
    private static CommentsResponseDto of(Comment comment) {
        CommentsResponseDto commentsResponseDto = new CommentsResponseDto();
        commentsResponseDto.id = comment.getId();
        commentsResponseDto.content = comment.getContent();
        commentsResponseDto.xLocation = comment.getXLocation();
        commentsResponseDto.yLocation = comment.getYLocation();
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
