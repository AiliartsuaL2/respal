package com.hckst.respal.resume.presentation.dto.response;


import com.hckst.respal.comment.presentation.dto.response.CommentsResponseDto;
import com.hckst.respal.converter.TFCode;
import com.hckst.respal.resume.domain.Resume;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Builder
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

    private List<CommentsResponseDto> commentList;

    public ResumeDetailResponseDto(Resume resume, List<CommentsResponseDto> commentList) {
        this.title = resume.getTitle();
        this.content = resume.getContent();
        this.filePath = resume.getFilePath();
        this.membersId = resume.getMembers().getId();
        this.membersNickname = resume.getMembers().getNickname();
        this.membersPicture = resume.getMembers().getPicture();
        this.mainYn = TFCode.TRUE.equals(resume.getMainYn()) ? "Y" : "N";
        this.modifyYn = TFCode.TRUE.equals(resume.getModifyYn()) ? "Y" : "N";
        this.regTime = resume.getRegTime() != null ? resume.getRegTime().format(DateTimeFormatter.ofPattern("yyyyMMdd")) : null;
        this.modifyTime = resume.getModifyTime() != null ? resume.getRegTime().format(DateTimeFormatter.ofPattern("yyyyMMdd")) : null;
        this.commentList = commentList;
    }
}
