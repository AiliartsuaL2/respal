package com.hckst.respal.comment.presentation.dto.request;

import com.hckst.respal.members.domain.Members;
import com.hckst.respal.resume.domain.Resume;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCommentRequestDto {
    private String content;
    private int xLocation;
    private int yLocation;
}
