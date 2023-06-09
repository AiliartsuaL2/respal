package com.hckst.respal.authentication;


import com.hckst.respal.authentication.oauth.presentation.dto.response.TestResponseDto;
import com.hckst.respal.global.dto.ApiCommonResponse;
import com.hckst.respal.global.dto.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testController {

    @Operation(summary = "Authorization test 메서드", description = "인가 테스트 메서드 입니다. 인가에 성공시 ok 반환, 실패시 401 에러를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "기존 회원, 신규회원에 accessToken, refreshToken 추가", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "토큰 타입에 따라 에러 메세지가 나타납니다.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
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
