package com.hckst.respal.authentication;


import com.hckst.respal.authentication.oauth.presentation.dto.response.TestResponseDto;
import com.hckst.respal.global.dto.ApiCommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testController {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/test")
    public ResponseEntity<ApiCommonResponse<TestResponseDto>> test(){
        String message = "ok";
        TestResponseDto responseDto = TestResponseDto.builder()
                .message(message)
                .code(HttpStatus.OK.value())
                .build();
        ApiCommonResponse response = ApiCommonResponse.builder()
                .statusCode(200)
                .data(responseDto)
                .build();
        return ResponseEntity.ok(response);
    }
}
