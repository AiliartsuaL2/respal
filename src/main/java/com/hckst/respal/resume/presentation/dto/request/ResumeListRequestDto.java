package com.hckst.respal.resume.presentation.dto.request;

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
    /**
     * 매핑 조건
     * sort (정렬 기준)
     *   - recent
     *   - views
     *   - comments
     *
     * direction (차순)
     *   - asc
     *   - desc
     */
    public static ResumeListRequestDto createHubCondition(int page, long limit, String sort) {
        ResumeListRequestDto dto = new ResumeListRequestDto();
        dto.page = page == 0 ? 1 : page;
        dto.limit = limit == 0 ? 20 : limit;
        if("recent".equals(sort)){
            dto.sort = ResumeSort.RECENT_DESC;
        }else if("views".equals(sort)){
            dto.sort = ResumeSort.VIEWS_DESC;
        }
        dto.offset = (page-1)*limit;
        dto.resumeType = ResumeType.PUBLIC;
        return dto;
    }

    public static ResumeListRequestDto createTaggedCondition(int page, long limit, String sort, Members viewer) {
        ResumeListRequestDto dto = new ResumeListRequestDto();
        dto.page = page == 0 ? 1 : page;
        dto.limit = limit == 0 ? 20 : limit;
        if("recent".equals(sort)){
            dto.sort = ResumeSort.RECENT_DESC;
        }else if("views".equals(sort)){
            dto.sort = ResumeSort.VIEWS_DESC;
        }
        dto.offset = (page-1)*limit;
        dto.resumeType = ResumeType.PRIVATE;
        dto.viewer = viewer;
        return dto;
    }
}
