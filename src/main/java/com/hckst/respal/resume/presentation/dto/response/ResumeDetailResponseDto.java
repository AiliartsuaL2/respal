package com.hckst.respal.resume.presentation.dto.response;


import com.hckst.respal.comment.domain.Comment;
import com.hckst.respal.comment.presentation.dto.response.CommentsResponseDto;
import com.hckst.respal.converter.TFCode;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Schema(description = "이력서 상세 조회 DTO")
public class ResumeDetailResponseDto {
    // 이력서 ID
    private Long resumeId;
    // 이력서 제목
    private String title;
    // 이력서 내용
    private String content;
    // 이력서 파일 경로
    private String filePath;
    // 작성자 id
    private Long membersId;
    // 작성자 닉네임
    private String membersNickname;
    // 작성자 프로필 이미지
    private String membersPicture;
    // 대표여부
    private String mainYn;
    // 수정여부
    private String modifyYn;
    // 작성시간
    private String regTime;
    // 수정시간
    private String modifyTime;

    private List<Comment> commentList;

    @QueryProjection
    @Builder
    public ResumeDetailResponseDto(Long resumeId, String title, String content, String filePath, Long membersId, String membersNickname, String membersPicture, TFCode mainYn, TFCode modifyYn, LocalDateTime regTime, LocalDateTime modifyTime, List<Comment> commentList) {
        this.resumeId = resumeId;
        this.title = title;
        this.content = content;
        this.filePath = filePath;
        this.membersId = membersId;
        this.membersNickname = membersNickname;
        this.membersPicture = membersPicture;
        this.mainYn = TFCode.TRUE.equals(mainYn) ? "Y" : "N";
        this.modifyYn = TFCode.TRUE.equals(modifyYn) ? "Y" : "N";
        this.regTime = regTime != null ? regTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")) : null;
        this.modifyTime = modifyTime != null ? modifyTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")) : null;
        this.commentList = commentList;
    }

}
