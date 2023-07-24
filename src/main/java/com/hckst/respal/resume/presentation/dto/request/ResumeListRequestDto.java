package com.hckst.respal.resume.presentation.dto.request;

import com.hckst.respal.converter.ResumeSort;
import com.hckst.respal.members.domain.Job;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Schema(description = "이력서 리스트 요청 DTO")
public class ResumeListRequestDto {
    // 페이지 넘버
    @NotNull
    private int page;
    // 요청당 페이지 수
    @NotNull
    private long limit;
    // 쿼리에 사용될 offset / (page-1) * limit
    private long offset;

    // 필터( 직업)
    private int jobId;
    // 정렬 조건
    private ResumeSort sort;

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
    public ResumeListRequestDto(int page, long limit, int jobId, String sort, String direction) {
        this.page = page == 0 ? 1 : page;
        this.limit = limit == 0 ? 20 : limit;
        this.jobId = jobId;
        if("recent".equals(sort)){
            this.sort = "asc".equals(direction) ? ResumeSort.RECENT_ASC : ResumeSort.RECENT_DESC;
        }else if("views".equals(sort)){
            this.sort = "asc".equals(direction) ? ResumeSort.VIEWS_ASC : ResumeSort.VIEWS_DESC;
        }else if("comments".equals(sort)){
            this.sort = "asc".equals(direction) ? ResumeSort.COMMENTS_ASC : ResumeSort.COMMENTS_DESC;
        }
        this.offset = (page-1)*limit;
    }
}
