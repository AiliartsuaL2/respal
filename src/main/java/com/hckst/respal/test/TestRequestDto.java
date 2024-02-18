package com.hckst.respal.test;

import lombok.Getter;

@Getter
public class TestRequestDto {
    private String callbackUrl;
    private String videoUrl;
    private int analysisStartFrame;
    private int analysisLastFrame;
}
