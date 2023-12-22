package com.hckst.respal.resume.presentation.dto.response;


import com.hckst.respal.comment.domain.Comment;
import com.hckst.respal.comment.presentation.dto.response.CommentsResponseDto;
import com.hckst.respal.converter.TFCode;
import com.hckst.respal.resume.domain.Resume;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    private String fileName;
    // 조회수
    private int views;
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
    // 이력서 리스트를 위한 댓글 개수
    private long commentCount;

    @QueryProjection
    public ResumeDetailResponseDto(Long resumeId, String title, String content, int views, Long membersId, String membersNickname, String membersPicture, TFCode mainYn, TFCode modifyYn, LocalDateTime regTime, LocalDateTime modifyTime) {
        this.resumeId = resumeId;
        this.title = title;
        this.content = content;
        this.views = views;
        this.membersId = membersId;
        this.membersNickname = membersNickname;
        this.membersPicture = membersPicture;
        this.mainYn = TFCode.TRUE.equals(mainYn) ? "Y" : "N";
        this.modifyYn = TFCode.TRUE.equals(modifyYn) ? "Y" : "N";
        this.regTime = regTime != null ? regTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")) : null;
        this.modifyTime = modifyTime != null ? modifyTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")) : null;
    }

    @Builder
    public ResumeDetailResponseDto(Resume resume) {
        this.resumeId = resume.getId();
        this.title = resume.getTitle();
        this.content = resume.getContent();
        this.filePath = resume.getResumeFile().getAccessUrl();
        this.fileName = resume.getResumeFile().getOriginName();
        this.views = resume.getViews();
        this.membersId = resume.getMembers().getId();
        this.membersNickname = resume.getMembers().getNickname();
        this.membersPicture = resume.getMembers().getPicture();
        this.mainYn = TFCode.TRUE.equals(resume.getMainYn()) ? "Y" : "N";
        this.modifyYn = TFCode.TRUE.equals(resume.getModifyYn()) ? "Y" : "N";
        this.regTime = resume.getRegTime() != null ? resume.getRegTime().format(DateTimeFormatter.ofPattern("yyyyMMdd")) : null;
        this.modifyTime = resume.getModifyTime() != null ? resume.getRegTime().format(DateTimeFormatter.ofPattern("yyyyMMdd")) : null;
    }
}
