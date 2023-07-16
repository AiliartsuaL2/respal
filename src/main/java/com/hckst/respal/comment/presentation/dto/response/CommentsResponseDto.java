package com.hckst.respal.comment.presentation.dto.response;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentsResponseDto {
    private Long id;
    private String content;
    private int xLocation;
    private int yLocation;
    private Long membersId;
    private String membersPicture;
    private String membersNickname;
}
