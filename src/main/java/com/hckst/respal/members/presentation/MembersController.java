package com.hckst.respal.members.presentation;

import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.authentication.jwt.dto.response.RefreshAccessTokenResponseDto;
import com.hckst.respal.authentication.jwt.application.JwtService;
import com.hckst.respal.authentication.oauth.application.OAuthService;
import com.hckst.respal.converter.Provider;
import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.global.dto.ApiCommonResponse;
import com.hckst.respal.members.presentation.dto.request.*;
import com.hckst.respal.global.dto.ApiErrorResponse;
import com.hckst.respal.members.application.MembersService;
import com.hckst.respal.members.presentation.dto.request.SearchMembersRequestDto;
import com.hckst.respal.members.presentation.dto.response.MembersResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@Tag(name = "회원", description = "회원 관련 api")
public class MembersController {
    private final MembersService membersService;
    private final OAuthService oAuthService;
    private final JwtService jwtService;

    @GetMapping("/member/login")
    public String loginPage(){
        return "member/login.html";
    }

    @Operation(summary = "로그인 메서드", description = "일반 이메일 로그인 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "로그인 실패(올바르지 않은 사용자 정보)", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/member/login")
    @ResponseBody
    public ResponseEntity<ApiCommonResponse<Token>> login(@RequestBody MembersLoginRequestDto membersLoginRequestDto){
        Token token = membersService.login(membersLoginRequestDto);

        ApiCommonResponse<Token> response = new ApiCommonResponse<>(200, token);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원가입 메서드", description = "일반 이메일 회원가입 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "회원가입 실패 (중복된 이메일)", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/member/join")
    @ResponseBody
    public ResponseEntity<ApiCommonResponse<String>> join(@Valid @RequestBody MembersJoinRequestDto membersJoinRequestDto){
        oAuthService.join(membersJoinRequestDto);

        ApiCommonResponse<String> response = new ApiCommonResponse<>(201, "회원가입이 성공적으로 처리되었어요");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "회원가입시 이메일 인증 메서드", description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "access 토큰 재발급", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "access 토큰 재발급 실패(올바르지 않은 refresh token)", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/member/email")
    @ResponseBody
    public ResponseEntity<ApiCommonResponse<String>> sendEmailForJoin(@RequestParam String email){
        if(membersService.checkMembers(email)){
            throw new ApplicationException(ErrorMessage.DUPLICATE_EMAIL_EXCEPTION);
        }
        ApiCommonResponse<String> response = new ApiCommonResponse<>(200, "인증번호 확인 이메일을 전송하였어요.");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "access token 재발급 메서드", description = "access token 재발급 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "access 토큰 재발급", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "access 토큰 재발급 실패(올바르지 않은 refresh token)", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/jwt/refresh")
    @ResponseBody
    public ResponseEntity<ApiCommonResponse<RefreshAccessTokenResponseDto>> refreshAccessToken(@RequestHeader(value = "Authorization") String refreshToken){
        RefreshAccessTokenResponseDto responseDto = jwtService.renewAccessToken(refreshToken);

        ApiCommonResponse<RefreshAccessTokenResponseDto> response = new ApiCommonResponse<>(200, responseDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "임시 비밀번호 이메일 전송 메서드", description = "임시 비밀번호 설정 후 이메일로 임시 비밀번호를 전송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 전송 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "이메일 전송 실패", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/password")
    @ResponseBody
    // 이메일로 비밀번호 재설정 다이렉션 전송
    public ResponseEntity<ApiCommonResponse<String>> findPassword(@RequestBody SendEmailRequestDto sendEmailRequestDto){
        String password = membersService.passwordResetToTmp(sendEmailRequestDto.getEmail());
        sendEmailRequestDto.setTmpPassword(password);
        membersService.sendPasswordResetEmail(sendEmailRequestDto);

        ApiCommonResponse<String> response = new ApiCommonResponse<>(200, "해당 이메일로 임시 비밀번호를 전송하였어요.");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "비밀번호 재설정 메서드", description = "비밀번호 재설정을 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "비밀번호 재설정 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "비밀번호 재설정 실패", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PatchMapping("/password")
    @ResponseBody
    public ResponseEntity<ApiCommonResponse<String>> updatePassword(
            @RequestBody
            @Valid
            ResetPasswordRequestDto resetPasswordRequestDto){
        membersService.updatePassword(resetPasswordRequestDto);

        ApiCommonResponse<String> response = new ApiCommonResponse<>(204, "비밀번호가 성공적으로 재설정 되었어요");
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    // refresh token만 db에서 삭제
    @Operation(summary = "로그아웃 메서드", description = "로그아웃 메서드입니다. Database에서 refreshToken을 제거합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "refresh token 제거 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "refresh token 확인 불가", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/member/logout")
    @ResponseBody
    public ResponseEntity<ApiCommonResponse<String>> logout(@RequestHeader(value = "Authorization") String refreshToken){
        oAuthService.logout(refreshToken);

        ApiCommonResponse<String> response = new ApiCommonResponse<>(204, "로그아웃이 정상적으로 처리되었어요.");
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    // 닉네임을 이용한 사용자 검색용 메서드
    @Operation(summary = "멘션시 필요한 사용자 검색용 메서드", description = "사용자 검색용 메서드 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "멘션에 필요한 사용자 정보들을 포함한 리스트를 응답합니다.", useReturnTypeSchema = true)
    })
    @GetMapping("/members")
    @ResponseBody
    public ResponseEntity<ApiCommonResponse<List<MembersResponseDto>>> searchMembers(@RequestParam String searchWord, @RequestParam int limit){
        SearchMembersRequestDto searchMembersRequestDto = SearchMembersRequestDto.builder()
                .limit(limit)
                .searchWord(searchWord)
                .build();

        List<MembersResponseDto> searchMembers = membersService.searchMembers(searchMembersRequestDto);

        ApiCommonResponse<List<MembersResponseDto>> response = new ApiCommonResponse<>(200, searchMembers);
        return ResponseEntity.ok(response);
    }
}
