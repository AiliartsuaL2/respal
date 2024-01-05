package com.hckst.respal.resume.presentation.dto.request;

import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.resume.domain.ResumeSort;
import com.hckst.respal.converter.ResumeType;
import com.hckst.respal.members.domain.Members;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Schema(description = "이력서 리스트 요청 DTO")
@NoArgsConstructor
public class ResumeListRequestDto {
    // 페이지 넘버
    @NotNull(message = "페이지 번호는 필수 입력값이에요.")
    private int page;
    // 요청당 페이지 수
    @NotNull(message = "한 페이지당 게시물 수는 필수 입력값이에요")
    private long limit;
    // 쿼리에 사용될 offset / (page-1) * limit
    private long offset;
    // 정렬 조건
    private ResumeSort sort;
    private ResumeType resumeType;
    // 조회하는 회원, tagged 에서만 필요.
    private Members viewer;

    public static ResumeListRequestDto create(String type, int page, long limit, Members viewer) {
        ResumeType resumeType = ResumeType.findByType(type);
        checkMember(resumeType, viewer);

        ResumeListRequestDto dto = new ResumeListRequestDto();
        dto.page = page == 0 ? 1 : page;
        dto.limit = limit == 0 ? 20 : limit;
        dto.sort = ResumeSort.RECENT_DESC;
        dto.offset = (page-1)*limit;
        dto.resumeType = resumeType;
        dto.viewer = viewer;
        return dto;
    }

    private static void checkMember(ResumeType resumeType, Members viewer) {
        if(ResumeType.PUBLIC.equals(resumeType)) {
            return;
        }
        if(viewer == null) {
            throw new ApplicationException(ErrorMessage.PERMISSION_DENIED_TO_VIEW_EXCEPTION);
        }
    }
}
