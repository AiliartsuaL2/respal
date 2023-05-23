package com.hckst.respal.members.presentation;

import com.hckst.respal.authentication.jwt.dto.response.RefreshAccessTokenResponseDto;
import com.hckst.respal.authentication.jwt.service.JwtService;
import com.hckst.respal.members.presentation.dto.request.MembersLoginRequestDto;
import com.hckst.respal.members.presentation.dto.request.MembersJoinRequestDto;
import com.hckst.respal.exception.dto.ApiErrorResponse;
import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.members.application.MembersService;
import com.hckst.respal.members.presentation.dto.response.MembersJoinResponseDto;
import com.hckst.respal.members.presentation.dto.response.MembersLoginResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Controller
@Slf4j
@RequiredArgsConstructor
@Tag(name = "회원", description = "회원 관련 api")
public class MembersController {
    private final MembersService membersService;
    private final JwtService jwtService;

    @GetMapping("/member/login")
    public String loginPage(){
        return "member/login.html";
    }


    @Operation(summary = "로그인 메서드", description = "일반 이메일 로그인 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = MembersLoginResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "로그인 실패(올바르지 않은 사용자 정보)", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/member/login")
    @ResponseBody
    public ResponseEntity<MembersLoginResponseDto> login(@RequestBody MembersLoginRequestDto membersLoginRequestDto){
        Token token = membersService.loginMembers(membersLoginRequestDto);
        jwtService.login(token);
        MembersLoginResponseDto response = MembersLoginResponseDto.builder()
                .membersEmail(token.getMembersEmail())
                .refreshToken(token.getRefreshToken())
                .accessToken(token.getAccessToken())
                .grantType(token.getGrantType())
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원가입 메서드", description = "일반 이메일 회원가입 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공", headers = @Header(name = "Location", description = "리다이렉트 Url"), content = @Content(schema = @Schema(implementation = MembersJoinResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "회원가입 실패 (중복된 이메일)", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/member/join")
    @ResponseBody
    public ResponseEntity<MembersJoinResponseDto> join(@RequestBody MembersJoinRequestDto membersJoinRequestDto){
        membersService.joinMembers(membersJoinRequestDto);
        URI redirectUrl = URI.create(String.format("/member", "/login"));
        return ResponseEntity.created(redirectUrl).body(new MembersJoinResponseDto()); // PRG 방식
    }

    @Operation(summary = "access token 재발급 메서드", description = "access token 재발급 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "access 토큰 재발급", content = @Content(schema = @Schema(implementation = MembersLoginResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "access 토큰 재발급 실패(올바르지 않은 refresh token)", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })

    @PostMapping("/jwt/refresh")
    @ResponseBody
    public ResponseEntity<RefreshAccessTokenResponseDto> refreshAccessToken(@RequestHeader(value = "Authorization") String refreshToken){
        RefreshAccessTokenResponseDto response = jwtService.validateRefreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

}
