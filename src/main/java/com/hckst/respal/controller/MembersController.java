package com.hckst.respal.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MembersController {
    @GetMapping("/member/login")
    public String loginPage(){
        return "/member/login.html";
    }

}
