package com.hckst.respal.authentication;


import com.hckst.respal.authentication.oauth.dto.response.TestResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testController {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/test")
    public ResponseEntity<TestResponseDto> test(){
        String message = "ok";
        TestResponseDto response = TestResponseDto.builder()
                .message(message)
                .code(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(response);
    }
}
