package com.hckst.respal.controller;

import com.hckst.respal.common.exception.ErrorMessage;
import com.hckst.respal.dto.MemberJoinDto;
import com.hckst.respal.dto.response.ResponseDto;
import com.hckst.respal.jwt.dto.Token;
import com.hckst.respal.service.MembersService;
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
public class MembersController {
    private final MembersService membersService;
    @GetMapping("/member/login")
    public String loginPage(){
        return "member/login.html";
    }

    @PostMapping("/member/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody MemberJoinDto memberJoinDto){
        Token token = membersService.loginMembers(memberJoinDto);
        if(token == null){
            throw new NoSuchElementException(ErrorMessage.INCORRECT_MEMBER_INFO.getMsg());
        }
        ResponseDto response = ResponseDto.builder()
                .success(true)
                .code(200)
                .data(token)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/member/join")
    @ResponseBody
    public ResponseEntity<?> join(@RequestBody MemberJoinDto memberJoinDto){
        if(membersService.duplicationCheckEmail(memberJoinDto.getEmail())){
            throw new RejectedExecutionException(ErrorMessage.DUPLICATE_MEMBER_EMAIL.getMsg());
        }
        membersService.joinMembers(memberJoinDto);
        ResponseDto response = ResponseDto.builder()
                .success(true)
                .code(201)
                .data("회원가입이 정상적으로 되었습니다.")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
