package com.hckst.respal.members.domain.repository.dto;

import com.hckst.respal.converter.Provider;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

@Getter
@Setter
public class MembersOAuthDto {
    private Long id;
    private String email;
    @Builder
    @QueryProjection
    public MembersOAuthDto(Long id, String email){
        this.id = id;
        this.email = email;
    }
}
