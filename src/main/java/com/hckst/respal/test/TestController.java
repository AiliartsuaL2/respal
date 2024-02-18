package com.hckst.respal.test;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final TestService testService;

    @PostMapping("/ai/analysis")
    public ResponseEntity<TestResponseDto> analysis(@RequestBody TestRequestDto testRequestDto) {
        TestResponseDto analysis = testService.analysis(testRequestDto);
        return ResponseEntity.ok(analysis);
    }
}
