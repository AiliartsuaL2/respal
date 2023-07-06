package com.hckst.respal.members.presentation;

import com.hckst.respal.authentication.jwt.dto.response.RefreshAccessTokenResponseDto;
import com.hckst.respal.authentication.jwt.application.JwtService;
import com.hckst.respal.authentication.oauth.application.OAuthServiceImpl;
import com.hckst.respal.converter.Provider;
import com.hckst.respal.converter.ProviderConverter;
import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.global.dto.ApiCommonResponse;
import com.hckst.respal.members.domain.Job;
import com.hckst.respal.members.domain.repository.JobRepository;
import com.hckst.respal.members.presentation.dto.request.MembersLoginRequestDto;
import com.hckst.respal.members.presentation.dto.request.MembersJoinRequestDto;
import com.hckst.respal.global.dto.ApiErrorResponse;
import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.members.application.MembersService;
import com.hckst.respal.members.presentation.dto.request.PasswordPatchRequestDto;
import com.hckst.respal.members.presentation.dto.request.SendEmailRequestDto;
import com.hckst.respal.members.presentation.dto.response.MembersLoginResponseDto;
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
    private final OAuthServiceImpl oAuthService;
    private final JwtService jwtService;
    private final JobRepository jobRepository;

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
    public ResponseEntity<ApiCommonResponse<MembersLoginResponseDto>> login(@RequestBody MembersLoginRequestDto membersLoginRequestDto){
        Token token = membersService.loginMembers(membersLoginRequestDto);
        jwtService.login(token);
        String tmpPasswordStatus = membersService.checkTmpPasswordStatus(membersLoginRequestDto);
        MembersLoginResponseDto responseDto = MembersLoginResponseDto.builder()
                .membersEmail(token.getMembersEmail())
                .refreshToken(token.getRefreshToken())
                .accessToken(token.getAccessToken())
                .grantType(token.getGrantType())
                .tmpPasswordStatus(tmpPasswordStatus)
                .build();
        ApiCommonResponse response = ApiCommonResponse.builder()
                .statusCode(200)
                .data(responseDto)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "직업 목록 조회 API", description = "직업 목록 조회 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "직업 목록 조회 성공", useReturnTypeSchema = true)
    })
    @GetMapping("/job")
    @ResponseBody
    public ResponseEntity<ApiCommonResponse<List<Job>>> jobs(){
        List<Job> jobs = jobRepository.findAll();
        ApiCommonResponse response = ApiCommonResponse.builder()
                .statusCode(200)
                .data(jobs)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원가입 메서드", description = "일반 이메일 회원가입 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "회원가입 실패 (중복된 이메일)", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/member/join")
    @ResponseBody
    public ResponseEntity<ApiCommonResponse<MembersLoginResponseDto>> join(@Valid @RequestBody MembersJoinRequestDto membersJoinRequestDto){
        // provider type 없는경우 exception
        if(membersJoinRequestDto.getProvider() == null){
            throw new ApplicationException(ErrorMessage.NOT_EXIST_PROVIDER_TYPE);
        }
        ProviderConverter pc = new ProviderConverter();
        Provider provider = pc.convertToEntityAttribute(membersJoinRequestDto.getProvider());
        Token token = oAuthService.join(provider, membersJoinRequestDto);
        jwtService.login(token); // refresh 토큰 초기화

        MembersLoginResponseDto responseDto = MembersLoginResponseDto.builder()
                .membersEmail(token.getMembersEmail())
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .grantType(token.getGrantType())
                .build();

        ApiCommonResponse response = ApiCommonResponse.builder()
                .statusCode(201)
                .data(responseDto)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @Operation(summary = "이메일 확인 메서드", description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "access 토큰 재발급", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "access 토큰 재발급 실패(올바르지 않은 refresh token)", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/member/join")
    @ResponseBody
    public ResponseEntity<ApiCommonResponse<String>> sendEmailForJoin(SendEmailRequestDto sendEmailRequestDto){
        if(membersService.checkMembers(sendEmailRequestDto)){
            throw new ApplicationException(ErrorMessage.DUPLICATE_EMAIL_EXCEPTION);
        }
        ApiCommonResponse response = ApiCommonResponse.builder()
                .statusCode(200)
                .data("인증번호 확인 이메일을 전송하였습니다.")
                .build();
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
        RefreshAccessTokenResponseDto responseDto = jwtService.validateRefreshToken(refreshToken);

        ApiCommonResponse response = ApiCommonResponse.builder()
                .statusCode(200)
                .data(responseDto)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "임시 비밀번호 이메일 전송 메서드", description = "임시 비밀번호 설정 후 이메일로 임시 비밀번호를 전송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 전송 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "이메일 전송 실패", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/password")
    @ResponseBody
    // 이메일로 비밀번호 재설정 다이렉션 전송
    public ResponseEntity<ApiCommonResponse<String>> findPassword(@RequestBody SendEmailRequestDto sendEmailRequestDto){
        String password = membersService.passwordResetToTmp(sendEmailRequestDto.getEmail());
        sendEmailRequestDto.setTmpPassword(password);
        membersService.sendPasswordResetEmail(sendEmailRequestDto);
        ApiCommonResponse response = ApiCommonResponse.builder()
                .statusCode(200)
                .data("해당 이메일로 임시 비밀번호를 전송하였습니다.")
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "비밀번호 재설정 메서드", description = "비밀번호 재설정을 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 재설정 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "비밀번호 재설정 실패", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PatchMapping("/password")
    @ResponseBody
    public ResponseEntity<ApiCommonResponse<String>> updatePassword(@RequestBody PasswordPatchRequestDto passwordPatchRequestDto){
        membersService.updatePassword(passwordPatchRequestDto);
        ApiCommonResponse response = ApiCommonResponse.builder()
                .statusCode(200)
                .data("비밀번호를 성공적으로 변경하였습니다.")
                .build();
        return ResponseEntity.ok(response);
    }

    // refresh token만 db에서 삭제
    @Operation(summary = "로그아웃 메서드", description = "로그아웃 메서드입니다. Database에서 refreshToken을 제거합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "refresh token 제거 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "refresh token 확인 불가", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/member/logout")
    @ResponseBody
    public ResponseEntity<ApiCommonResponse<String>> logout(@RequestHeader(value = "Authorization") String refreshToken){
        oAuthService.logout(refreshToken);
        ApiCommonResponse response = ApiCommonResponse.builder()
                .statusCode(200)
                .data("정상적으로 로그아웃이 되었습니다.")
                .build();
        return ResponseEntity.ok(response);
    }
}
