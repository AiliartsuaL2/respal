package com.hckst.respal.members.presentation;

import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.members.presentation.dto.MemberJoinDto;
import com.hckst.respal.exception.dto.ApiErrorResponse;
import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.members.application.MembersService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.NoSuchElementException;
import java.util.concurrent.RejectedExecutionException;

@Controller
@Slf4j
@RequiredArgsConstructor
@Tag(name = "회원", description = "회원 관련 api")
public class MembersController {
    private final MembersService membersService;
    @GetMapping("/member/login")
    public String loginPage(){
        return "member/login.html";
    }


    @Operation(summary = "로그인 메서드", description = "일반 이메일 로그인 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "로그인 실패(올바르지 않은 사용자 정보)", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/member/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody MemberJoinDto memberJoinDto){
        Token token = membersService.loginMembers(memberJoinDto);
        if(token == null){
            throw new NoSuchElementException(ErrorMessage.INCORRECT_MEMBER_INFO.getMsg());
        }
        ApiErrorResponse response = ApiErrorResponse.builder()
                .success(true)
                .code(200)
                .data(token)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Operation(summary = "회원가입 메서드", description = "일반 이메일 회원가입 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "회원가입 실패 (중복된 이메일)", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/member/join")
    @ResponseBody
    public ResponseEntity<?> join(@RequestBody MemberJoinDto memberJoinDto){
        if(membersService.duplicationCheckEmail(memberJoinDto.getEmail())){
            throw new RejectedExecutionException(ErrorMessage.DUPLICATE_MEMBER_EMAIL.getMsg());
        }
        membersService.joinMembers(memberJoinDto);
        ApiErrorResponse response = ApiErrorResponse.builder()
                .success(true)
                .code(201)
                .data("회원가입이 정상적으로 되었습니다.")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
