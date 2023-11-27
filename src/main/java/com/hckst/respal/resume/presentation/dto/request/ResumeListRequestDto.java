package com.hckst.respal.resume.presentation.dto.request;

import com.hckst.respal.resume.domain.ResumeSort;
import com.hckst.respal.converter.ResumeType;
import com.hckst.respal.members.domain.Members;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Schema(description = "이력서 리스트 요청 DTO")
public class ResumeListRequestDto {
    // 페이지 넘버
    @NotNull(message = "페이지 번호는 필수 입력값이에요.")
    private int page;
    // 요청당 페이지 수
    @NotNull(message = "한 페이지당 게시물 수는 필수 입력값이에요")
    private long limit;
    // 쿼리에 사용될 offset / (page-1) * limit
    private long offset;

    // 필터( 직업)
    private Integer jobId;
    // 정렬 조건
    private ResumeSort sort;

    private ResumeType resumeType;
    // 조회하는 회원, tagged 에서만 필요.
    private Members viewer;

    public void setViewer(Members viewer){
        this.viewer = viewer;
    }
    public void setHubCondition(){
        this.resumeType = ResumeType.PUBLIC;
    }
    public void setTaggedCondition(){
        this.resumeType = ResumeType.PRIVATE;
    }
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
    @Builder
    public ResumeListRequestDto(int page, long limit, Integer jobId, String sort, String direction) {
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
