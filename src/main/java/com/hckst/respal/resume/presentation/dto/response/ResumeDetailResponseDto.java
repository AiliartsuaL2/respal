package com.hckst.respal.resume.presentation.dto.response;

import com.hckst.respal.converter.TFCode;
import com.hckst.respal.resume.domain.Resume;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Schema(description = "이력서 상세 조회 DTO")
public class ResumeDetailResponseDto {
    @Schema(description = "이력서의 ID입니다.")
    private Long resumeId;
    @Schema(description = "이력서의 제목입니다.")
    private String title;
    @Schema(description = "이력서의 내용입니다.")
    private String content;
    @Schema(description = "이력서 파일의 경로입니다.")
    private String filePath;
    @Schema(description = "이력서 파일의 이름입니다.")
    private String fileName;
    @Schema(description = "이력서의 조회수 입니다.")
    private int views;
    @Schema(description = "이력서 작성자의 ID입니다.")
    private Long membersId;
    @Schema(description = "이력서 작성자의 닉네임 입니다.")
    private String membersNickname;
    @Schema(description = "이력서 작성자의 프로필 사진 입니다.")
    private String membersPicture;
    @Schema(description = "이력서의 수정 여부 입니다.")
    private String modifyYn;
    @Schema(description = "이력서 작성 시간입니다.")
    private String regTime;
    @Schema(description = "이력서의 수정 시간입니다.")
    private String modifyTime;
    @Schema(description = "이력서의 댓글 개수 입니다.")
    private long commentCount;

    @QueryProjection
    public ResumeDetailResponseDto(Long resumeId, String title, String content, int views, Long membersId, String membersNickname, String membersPicture, TFCode modifyYn, LocalDateTime regTime, LocalDateTime modifyTime) {
        this.resumeId = resumeId;
        this.title = title;
        this.content = content;
        this.views = views;
        this.membersId = membersId;
        this.membersNickname = membersNickname;
        this.membersPicture = membersPicture;
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
        this.modifyYn = TFCode.TRUE.equals(resume.getModifyYn()) ? "Y" : "N";
        this.regTime = resume.getRegTime() != null ? resume.getRegTime().format(DateTimeFormatter.ofPattern("yyyyMMdd")) : null;
        this.modifyTime = resume.getModifyTime() != null ? resume.getRegTime().format(DateTimeFormatter.ofPattern("yyyyMMdd")) : null;
    }
}
