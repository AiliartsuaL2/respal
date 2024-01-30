package com.hckst.respal.authentication;


import com.hckst.respal.authentication.oauth.presentation.dto.response.TestResponseDto;
import com.hckst.respal.global.dto.ApiCommonResponse;
import com.hckst.respal.global.dto.ApiErrorResponse;
import com.hckst.respal.members.domain.Members;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.time.Duration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class testController {

    @Operation(summary = "Authorization test 메서드", description = "인가 테스트 메서드 입니다. 인가에 성공시 ok 반환, 실패시 401 에러를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "기존 회원, 신규회원에 accessToken, refreshToken 추가", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "토큰 타입에 따라 에러 메세지가 나타납니다.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/test")
    public ResponseEntity<ApiCommonResponse<TestResponseDto>> test(@AuthenticationPrincipal Members members){
        String message = members.getNickname();
        TestResponseDto responseDto = TestResponseDto.builder()
                .message(message)
                .code(HttpStatus.OK.value())
                .build();
        ApiCommonResponse<TestResponseDto> response = new ApiCommonResponse<>(200, responseDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/data")
    public Flux<ServerSentEvent<String>> getData() {

        Mono<String> firstResponse = Mono.just("첫번째 응답입니다😆");
        Mono<String> secondResponse = Mono.just("두번째 응답입니다😎");

        Flux<ServerSentEvent<String>> responseStream = Flux.concat(
                firstResponse.map(data -> ServerSentEvent.<String>builder().data(data).build()),
                secondResponse.delayElement(Duration.ofSeconds(5)).map(data -> ServerSentEvent.<String>builder().data(data).build())
        );

        return responseStream;
    }

}
