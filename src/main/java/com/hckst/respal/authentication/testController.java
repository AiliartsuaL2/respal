package com.hckst.respal.authentication;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testController {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/test")
    public ResponseEntity<String> test(){
        String response = "ok";
        return ResponseEntity.ok(response);
    }
}
