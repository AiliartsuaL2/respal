package com.hckst.respal.test;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestService {
    public TestResponseDto analysis(TestRequestDto requestDto) {
        String[] endpoint = requestDto.getCallbackUrl().split("/");
        long idx = Long.parseLong(endpoint[endpoint.length-1]);
        if(idx % 2 == 0) {
            throw new TestException();
        }
        return new TestResponseDto("분석 요청 성공");
    }
}
